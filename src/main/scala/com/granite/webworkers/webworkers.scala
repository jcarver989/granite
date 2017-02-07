package com.granite.webworkers

import scala.scalajs.js

import org.scalajs.dom.raw.Worker

import upickle.default._
import upickle.default.Writer

sealed trait WebWorker {
  def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit

  def postMessage[T <: WebWorkerTask: Writer](aMessage: T): Unit
}

class WebWorkerImpl(url: String) extends WebWorker {
  val w = new Worker(url)

  def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit = {
    w.addEventListener("message", listener, useCapture)
  }

  def postMessage[T <: WebWorkerTask: Writer](aMessage: T): Unit = {
    w.postMessage(write(aMessage))
  }
}

class StubWebWorker() extends WebWorker {
  private val listeners = scala.collection.mutable.Buffer[WebWorkerMessage[String] => Unit]()
  override def addEventListener(
    listener: WebWorkerMessage[String] => Unit,
    useCapture: Boolean = false): Unit = {
    listeners += listener
  }

  override def postMessage[T <: WebWorkerTask: Writer](
    aMessage: T): Unit = {
    val msg = new js.Object {
      val data: String = write(aMessage)
    }.asInstanceOf[WebWorkerMessage[String]]
    listeners.foreach { l => l(msg) }
  }
}