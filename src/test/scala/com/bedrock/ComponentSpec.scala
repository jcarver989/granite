package com.bedrock

import org.scalatest._
import scalatags.JsDom.all._
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node

class ComponentSpec extends FunSpec with Matchers {
  val view = Component[String] { name =>
    div(s"You look great today, ${name}").render
  }

  it("should render a view") {
    view.render("Tom").isEqualNode(div(s"You look great today, Tom").render) shouldBe true
  }
}