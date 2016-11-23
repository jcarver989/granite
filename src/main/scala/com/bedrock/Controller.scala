package com.bedrock

/**
 * Controllers have one responsibility, they map a view event (T) into a request to change the application's state.
 * 
 *  The implementation of onEvent() should be a pure function that returns a StateChangeRequest.
 *  
 *  Controllers make Components easily reusable across projects by encapsulating the application specific event handlers.
 *  That way Components simply emit non-app specific events (ex a list item was added/removed) and Controllers handle
 *  translating that event into a change request for a specific application's state. 
 */
trait Controller[T, U] {
  def handleEvent(event: T): Unit = {
    events.fireEvent(onEvent(event))
  }
  protected def events: Events[AppEvent]
  protected def onEvent(event: T): StateChangeRequest[U]
}
