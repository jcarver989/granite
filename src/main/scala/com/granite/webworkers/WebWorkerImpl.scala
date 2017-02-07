package com.granite.webworkers

import org.scalajs.dom.raw.Worker
import upickle.default._

class WebWorkerImpl[T <: WebWorkerTask: Reader: Writer](url: String) extends AbstractWebWorker[T] {
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