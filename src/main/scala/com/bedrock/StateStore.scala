package com.bedrock

/**
 * Manages the Application's state.
 *
 * Listens for change requests and fires an event when the state changes.
 */
class StateStore[T](initialState: T, events: Events[AppEvent]) {
  private var cachedState: T = initialState

  def currentState(): T = cachedState

  def init(): Unit = {
    events.onEvent { e: StateChangeRequest[T] =>
      cachedState = e.mapCurrentStateToNewState(cachedState)
      events.fireEvent(StateChange(cachedState, e))
    }

    events.fireEvent(StateChange(currentState, AppLoad))
  }
}