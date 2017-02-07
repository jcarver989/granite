package com.granite.webworkers

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.ClassTag

import upickle.default._

/**
 * Abstract representation of a JS WebWorker that is scoped to process tasks of type T.
 *
 *  Open requests awaiting responses are tracked so that classes like WorkerPool can schedule
 *  tasks in a reasonable way.
 */
protected[this] abstract class AbstractWebWorker[T <: WebWorkerTask: Reader] {
  // A metaphorical "queue" of outgoing requests, keyed by the taskId, that we haven't yet received responses for
  private val openRequests = scala.collection.mutable.Map[String, T => Unit]()

  protected def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit

  protected def postMessage(aMessage: T): Unit

  // Call to start listening to requests
  def init(): AbstractWebWorker[T] = {
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