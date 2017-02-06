package com.granite.webworkers

import scala.scalajs.js
import scala.scalajs.js.JSApp

import org.scalajs.dom.webworkers.DedicatedWorkerGlobalScope.self

import upickle.default.Reader
import upickle.default.Writer
import upickle.default.read
import upickle.default.write

/**
 * An "instance" of a web-worker, i.e. the code that the worker runs internally.
 *
 *  A WebWorkerInstance responds to messages of type T (which may be a super-class/trait) and the response
 *  to the message is provided via the onMessage() method, which downstream applications are expected to implement themselves.
 *  Typically the type T should be a trait, which various case classes extend so that in onMessage() you may pattern
 *  match against them and take appropriate action.
 *
 *  Web workers should be created via a WorkerPool, which is passed a WebWorkerInstance via the constructor.
 *  Downstream applications should extend this class and override the onMessage() method to provide responses
 */
abstract class WebWorkerInstance[T <: WebWorkerTask: Reader: Writer] extends JSApp {
  val scriptDependencies: Seq[String]
  def onMessage(msg: T): T

  def main(): Unit = {
    self.addEventListener("message", { e: WebWorkerMessage[String] =>
      val response = onMessage(read[T](e.data))
      self.postMessage(write(response))
    })
  }
} 