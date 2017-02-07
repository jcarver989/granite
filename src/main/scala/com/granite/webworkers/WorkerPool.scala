package com.granite.webworkers

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

import org.scalajs.dom.URL
import org.scalajs.dom.raw.Blob
import org.scalajs.dom.raw.BlobPropertyBag
import org.scalajs.dom.raw.Worker
import org.scalajs.dom.window
import NavigatorExt._

import upickle.default._
import scala.reflect.ClassTag

/**
 * A fixed size "thread" pool of web-workers.
 *
 *  Incoming tasks, are sent to the worker that currently has the smallest
 *  number of open (unfulfilled) requests
 */
class WorkerPool[T <: WebWorkerTask: Reader: Writer](
    app: WebWorkerInstance[T],
    numWorkers: Int = window.navigator.hardwareConcurrency,
    rootDependencyPath: String = window.location.origin.toString) {
  // Then WebWorker API assumes you create an instance of a worker via passing
  // the constructor a String representing the path to an external JS file,
  // containing the code the worker will execute. To avoid having to use
  // external files, which is inconvienient for Scala.js, we instead
  // "inline" the workers by creating a Blob object containing the script's code
  // and pass its URL to the worker constructor.
  private val workerCode = Vector[js.Any](s"""
    ${app.scriptDependencies.map { dep => s"importScripts('${rootDependencyPath}/${dep}')" }.mkString("\n")}
    ${app.getClass.getName.replace("$", "().main()")}
 """).toJSArray

  private val blob = new Blob(
    workerCode,
    BlobPropertyBag("application/javascript"))

  private val workerUrl = URL.createObjectURL(blob)
  private val pool = (1 to numWorkers).map { _ => new WebWorkerImpl[T](workerUrl).init() }

  def run[U <: T: ClassTag](task: U): Future[U] = {
    val thread = pool.sortBy { _.nRunningTasks }.head
    thread.run(task)
  }
}
