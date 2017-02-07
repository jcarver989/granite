package com.granite.webworkers

import org.scalatest._
import scalatags.JsDom.all._
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node
import upickle.default._

case class FakeTask(id: String, result: String = "") extends WebWorkerTask
object FakeTask {
  implicit val pkl = upickle.default.macroRW[FakeTask]
}

class WebWorkerClientSpec extends FunSpec with Matchers {

  val worker = new StubWebWorker()
  val client = new WorkerClient[FakeTask](worker)

  it("should send and listen to messages") {

    //view.renderNode("Tom").isEqualNode(div(id := view.domId, s"You look great today, Tom").render) shouldBe true
  }
}
