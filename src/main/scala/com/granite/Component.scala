package com.granite

import org.scalajs.dom.raw.Node

/**
 * A reusable view.
 * 
 * Components have a pure function: render, which returns a DOM node given some input T
 */
trait Component[T] {
  def render(state: T): Node
}

/**
 * Allows creating small components inline
 *
 *  ex:
 * 	import scalatags.JsDom.all._
 *  val view = Component[String] { name => div("Hello" + name + ", how are you?").render }
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