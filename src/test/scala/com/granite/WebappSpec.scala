package com.granite

import org.scalatest._
import scalatags.JsDom.all._
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node

class WebappSpec extends FunSpec with Matchers {
  val testView = Component[String] { name =>
    div(s"Hello ${name}",
      span("how are you ", span(cls := "green", "today?"))).render
  }
  class TestApp() extends GraniteApp[String] {
    override protected val initialState = "Bob"
    override protected val view = testView
    def start(root: Node): Unit = renderer.start(root)
    def getEvents() = events
  }

  val app = new TestApp()

  it("should render hello") {
    val root = div().render
    app.start(root)
    root.isEqualNode(expectedView(testView.domId, "Bob")) shouldBe true
  }

  it("should re-render on state change") {
    val root = div().render
    app.start(root)
    val changeName: String => String = s => "Amy"
    app.getEvents.fireEvent(StateChangeRequest(changeName))
    root.isEqualNode(expectedView(testView.domId, "Amy")) shouldBe true
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