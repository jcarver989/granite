package com.granite

import org.scalajs.dom.raw.Node
import scala.scalajs.js
import org.scalajs.dom.raw.Element
import scala.concurrent.ExecutionContextExecutor
/**
 * Handles rendering the application to the DOM.
 *
 *  This implementation uses morphdom which will walk the real (not virtual) DOM
 *  to find the minimum diff to make the tree returned by view.render match the current
 *  state in the DOM
 */
class Renderer[T](initialState: T, view: Component[T], events: Events[AppEvent])(implicit executor: ExecutionContextExecutor) {
  def start(root: Node): Unit = {
    val store = new StateStore(initialState, events)
    events.onEvent { e: StateChange[T] =>
      morphdom(root, view.renderNode(e.state))
    }

    store.init()
  }
}

/** Scala wrapper around morphdom js library */
@js.native
object morphdom extends js.Object {
  def apply(target: Node, source: Node): Unit = js.native
}