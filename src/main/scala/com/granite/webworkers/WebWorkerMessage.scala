package com.granite.webworkers

import scala.scalajs.js

/**
 *
 * The Webworker API wraps whatever object you send in postMessage in a JS object like { data: [What you sent] },
 *  this is a wrapper class to make this easier to deal with
 */
@js.native
trait WebWorkerMessage[T] extends js.Object {
  val data: T = js.native
}
