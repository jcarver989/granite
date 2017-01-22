package com.granite

import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Element


/**
 * A reusable view.
 *
 * Components have a pure function: render, which returns a DOM node given some input T
 */
trait Component[T] {
  // Every instance of a component gets its own UUID
  // that will get attached to its corresponding DOM node (from the render() method).
  // this helps the diffing-algorithm differentiate between different components 
  // (ex multiple instances of the same component)
  val domId = java.util.UUID.randomUUID.toString
  protected def render(state: T): Element
  def renderNode(state: T): Element = {
    val output = render(state)
    output.setAttribute("id", domId)
    output
  }
}

/**
 * Allows creating small components inline
 *
 *  ex:
 * 	import scalatags.JsDom.all._
 *  val view = Component[String] { name => div("Hello" + name + ", how are you?").render }
 */
object Component {
  def apply[T](f: T => Element): Component[T] = {
    new Component[T] {
      override def render(state: T): Element = {
        f(state)
      }
    }
  }
}
