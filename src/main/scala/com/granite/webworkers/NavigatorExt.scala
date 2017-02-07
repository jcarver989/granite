package com.granite.webworkers

import scala.scalajs.js
import scala.language.implicitConversions
import org.scalajs.dom.raw.Navigator

/** Adds implicit to get the number of cores on the host machine (scala-js dom lib doesn't have this in their API yet) */
@js.native
trait NavigatorExt extends Navigator {
  val hardwareConcurrency: Int = js.native
}

object NavigatorExt {
  implicit def toExt(navigator: Navigator): NavigatorExt = {
    navigator.asInstanceOf[NavigatorExt]
  }
}