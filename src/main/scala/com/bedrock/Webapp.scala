package com.bedrock

import org.scalajs.dom.raw.Node

class Webapp[T](view: Component[T], initialState: T) {
  def start(root: Node): Unit = renderer.start(root)
  val events = new Events[AppEvent]()
  private lazy val renderer = new Renderer(initialState, view, events)
}