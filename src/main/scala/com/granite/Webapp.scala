package com.granite

import org.scalajs.dom.raw.Node
import scala.scalajs.js.JSApp
import scala.concurrent.ExecutionContext

trait GraniteApp[T] {
  protected implicit val executorContext = ExecutionContext.Implicits.global // scala.js default
  protected def view(): Component[T]
  protected def initialState(): T
  protected val events = new Events[AppEvent]()
  protected lazy val renderer = new Renderer(initialState, view, events)
}