package com.bedrock

import org.scalatest._
import scalatags.JsDom.all._
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node

class WebappSpec extends FunSpec with Matchers {
  val view = Component[String](expectedView)
  val app = new Webapp(view, "Bob")

  it("should render hello") {
    val root = div().render
    app.start(root)
    root.isEqualNode(expectedView("Bob")) shouldBe true
  }

  it("should re-render on state change") {
    val root = div().render
    app.start(root)
    val changeName: String => String = s => "Amy"
    app.events.fireEvent(StateChangeRequest(changeName))
    root.isEqualNode(expectedView("Amy")) shouldBe true
  }

  // Our app's view, extracted as a helper function here
  // so we can avoid having to use the "val view = ..." component's render()
  // method above.  
  private def expectedView(name: String): Node = {
    div(s"Hello ${name}",
      span("how are you ", span(cls := "green", "today?"))
    ).render
  }
}