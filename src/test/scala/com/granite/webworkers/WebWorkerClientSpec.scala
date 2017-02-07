package com.granite.webworkers

import org.scalatest._
import scalatags.JsDom.all._
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Node
import com.granite.BlockingExecutionContext._

case class FakeTask(id: String, result: String = "") extends WebWorkerTask
object FakeTask {
  import upickle.default._
  val pkl = upickle.default.macroRW[FakeTask]
}

class WebWorkerClientSpec extends AsyncFunSpec with Matchers {
  val worker = new StubWebWorker[FakeTask]({ t: FakeTask => t.copy(result = "Hello!") }).init()

  it("should send and listen to messages") {
    val result = worker.run(FakeTask("id-1"))
    result.map { r =>
      r shouldEqual FakeTask("id-1", "Hello!")
    }
  }
}
