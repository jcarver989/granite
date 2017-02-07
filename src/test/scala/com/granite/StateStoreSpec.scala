package com.granite

import org.scalatest._
import scala.concurrent.Future

class StateStoreSpec extends FunSpec with Matchers {
  import BlockingExecutionContext._
  it("should respond to state change requests") {
    val events = new StubEvents[AppEvent]()
    val store = new StateStore(Vector("some existing state"), events)
    store.init()

    val request: Vector[String] => Vector[String] = oldState => oldState :+ "some new state"
    val event = StateChangeRequest(request)

    events.fireEvent(event)
    events.firedEvents should contain(StateChange(Vector("some existing state", "some new state"), event))
  }
}