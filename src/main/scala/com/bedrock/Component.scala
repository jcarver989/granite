package com.bedrock

import org.scalajs.dom.raw.Node

/**
 * A reusable view that has a pure function (render), that returns a DOM node
 *  given some application state T
 */
trait Component[T] {
  def render(state: T): Node
}

/**
 * Convienence method to allow creating small components inline
 *
 *  ex:
 * 	import scalatags.JsDom.all._
 *  val view = View[String] { name => div(s"Hello ${name}, how are you?").render }
 */
object Component {
  def apply[T](f: T => Node): Component[T] = {
    new Component[T] {
      override def render(state: T): Node = {
        f(state)
      }
    }
  }
}