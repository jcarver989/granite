package com.granite.webworkers

import scala.scalajs.js

import org.scalajs.dom.raw.Worker

import upickle.default._
import scala.reflect.ClassTag
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.ExecutionContextExecutor

sealed abstract class WebWorker[T <: WebWorkerTask: Reader] {
  // A metaphorical "queue" of outgoing requests, keyed by the taskId, that we haven't yet received responses for
  private val openRequests = scala.collection.mutable.Map[String, T => Unit]()

  protected def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit

  protected def postMessage(aMessage: T): Unit

  def init(): WebWorker[T] = {
    // When the worker responds
    addEventListener { e: WebWorkerMessage[String] =>
      val task = read[T](e.data)
      openRequests(task.id)(task)
      openRequests.remove(task.id)
    }

    this
  }

  // Sends a request to a worker
  def run[U <: T: ClassTag](task: U): Future[U] = {
    val promise = Promise[U]()

    // Called when the worker responds back
    openRequests(task.id) = {
      case result: U => promise.success(result)
      case _         =>
    }

    postMessage(task)
    promise.future
  }

  def nRunningTasks: Int = openRequests.size
}

class WebWorkerImpl[T <: WebWorkerTask: Reader: Writer](url: String) extends WebWorker[T] {
  val w = new Worker(url)

  def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit = {
    w.addEventListener("message", listener, useCapture)
  }

  def postMessage(aMessage: T): Unit = {
    w.postMessage(write(aMessage))
  }
}

class StubWebWorker[T <: WebWorkerTask: Reader: Writer](fakeWorker: T => T) extends WebWorker[T] {
  private val listeners = scala.collection.mutable.Buffer[WebWorkerMessage[String] => Unit]()
  override def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit = {
    listeners += listener
  }

  override def postMessage(
    aMessage: T): Unit = {
    val msg = new js.Object {
      val data: String = write(fakeWorker(aMessage))
    }.asInstanceOf[WebWorkerMessage[String]]
    listeners.foreach { l => l(msg) }
  }
}
