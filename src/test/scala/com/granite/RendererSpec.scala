package com.granite

import org.scalatest._
import scalatags.JsDom.all._

import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node

class RendererSpec extends FunSpec with Matchers {
  import BlockingExecutionContext._

  val view = Component[String] { name =>
    div(
      s"Hello ${name}",
      span("how are you ", span(cls := "green", "today?"))).render
  }

  val events = new Events[AppEvent]()
  val renderer = new Renderer("Bob", view, events)

  it("should render hello") {
    val root = div().render
    renderer.start(root)
    root.isEqualNode(expectedView(view.domId, "Bob")) shouldBe true
  }

  it("should re-render on state change") {
    val root = div().render
    renderer.start(root)
    events.fireEvent(StateChange("Amy", AppLoad))
    root.isEqualNode(expectedView(view.domId, "Amy")) shouldBe true
  }

  // Our app's view, extracted as a helper function here
  // so we can avoid having to use the "val view = ..." component's render()
  // method above.  
  private def expectedView(domId: String, name: String): Element = {
    div(
      id := domId,
      s"Hello ${name}",
      span("how are you ", span(cls := "green", "today?"))).render
  }
}