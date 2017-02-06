package com.granite.webworkers

import scala.concurrent.Future
import scala.concurrent.Promise

import org.scalajs.dom.raw.Worker

import upickle.default._
import scala.reflect.ClassTag

/**
 * A "client" for a web-worker (background thread/process).
 *
 * The code here is executed on the main thread/process. This class should not be used by downstream applications, instead
 * create a WorkerPool (which uses this class internally). For the code that runs on the WebWorker,
 * see WebWorkerInstance.
 */

case class Callback[-T](f: T => Unit)
private[this] class WorkerClient[T <: Task: Reader](worker: Worker) {
  // A metaphorical "queue" of outgoing requests, keyed by the taskId, that we haven't yet received responses for
  private val openRequests = scala.collection.mutable.Map[String, Callback[_]]()

  // When the worker responds
  worker.addEventListener("message", { e: WebWorkerMessage[String] =>
    val task = read[T](e.data)
    val callback = openRequests(task.id)
    callback.asInstanceOf[Callback[T]].f(task)
    openRequests.remove(task.id)
  })

  // Sends a request to a worker
  def run[U <: T: Reader: Writer](task: U): Future[U] = {
    val promise = Promise[U]()

    // Called when the worker responds back
    openRequests(task.id) = Callback[U]((result) => {
      promise.success(result)
    })

    worker.postMessage(write(task))
    promise.future
  }

  def nRunningTasks: Int = openRequests.size
}
