package com.granite.webworkers

import scala.scalajs.js
import upickle.default._

class StubWebWorker[T <: WebWorkerTask: Reader: Writer](fakeWorker: T => T) extends AbstractWebWorker[T] {
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