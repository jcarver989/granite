package com.granite.webworkers

import scala.scalajs.js

import org.scalajs.dom.raw.Worker

import upickle.default._

sealed trait WebWorker[T <: WebWorkerTask] {
  def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit

  def postMessage(aMessage: T): Unit
}

class WebWorkerImpl[T <: WebWorkerTask : Writer](url: String) extends WebWorker[T] {
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

class StubWebWorker[T <: WebWorkerTask : Writer](fakeWorker: T => T) extends WebWorker[T] {
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
