package com.granite.webworkers

import scala.scalajs.js
import org.scalajs.dom.raw.Event

/**
 *
 * The Webworker API wraps whatever object you send in postMessage in a JS object like { data: [What you sent] },
 *  this is a wrapper class to make this easier to deal with
 */
@js.native
trait WebWorkerMessage[T] extends Event {
  val data: T = js.native
}
