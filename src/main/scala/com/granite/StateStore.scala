package com.granite

import scala.concurrent.ExecutionContextExecutor

/**
 * Manages the Application's state.
 *
 * Listens for change requests and fires an event when the state changes.
 */
class StateStore[T](initialState: T, events: Events[AppEvent])(implicit executor: ExecutionContextExecutor) {
  private var cachedState: T = initialState

  def currentState(): T = cachedState

  def init(): Unit = {
    events.onEvent { e: StateChangeRequest[T] =>
      e.mapCurrentStateToNewState(cachedState).foreach { state =>
        cachedState = state
        events.fireEvent(StateChange(cachedState, e))
      }
    }

    events.fireEvent(StateChange(currentState, AppLoad))
  }
}