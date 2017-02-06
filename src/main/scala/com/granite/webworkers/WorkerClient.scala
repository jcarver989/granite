package com.granite.webworkers

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.scalajs.js

import org.scalajs.dom.raw.Worker

import upickle.default._
import scala.reflect.ClassTag

private[this] class WorkerClient[T <: WebWorkerTask: Reader](worker: Worker) {
  // A metaphorical "queue" of outgoing requests, keyed by the taskId, that we haven't yet received responses for
  private val openRequests = scala.collection.mutable.Map[String, T => Unit]()

  // When the worker responds
  worker.addEventListener("message", { e: WebWorkerMessage[String] =>
    val task = read[T](e.data)
    openRequests(task.id)(task)
    openRequests.remove(task.id)
  })

  // Sends a request to a worker
  def run[U <: T: Reader: Writer: ClassTag](task: U): Future[U] = {
    val promise = Promise[U]()

    // Called when the worker responds back
    openRequests(task.id) = {
      case result: U => promise.success(result)
      case _         =>
    }

    worker.postMessage(write(task))
    promise.future
  }

  def nRunningTasks: Int = openRequests.size
}
