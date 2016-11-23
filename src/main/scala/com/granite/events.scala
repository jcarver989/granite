package com.granite

import scala.collection.mutable.Buffer
import scala.reflect.{ ClassTag, classTag }

/** Represents an event fired by the app. All application events should extend this  */
trait AppEvent

/** Fired when the app first loads */
case object AppLoad extends AppEvent

/**
 * Fired from a component to ask the StateStore to change the Application's state.
 *
 *  Since view components don't have direct access to the state, they send a function
 *  that accepts the "current" state and returns the "new" state.
 */
case class StateChangeRequest[T](mapCurrentStateToNewState: (T) => T) extends AppEvent

/** Fired from the StateStore when the Application state changes */
case class StateChange[T](state: T, triggeredBy: AppEvent) extends AppEvent

/**
 * Event bus where callers can fire or listen to events.
 *
 * Events do not use DOM events so any Scala object can be used as the event
 */
class Events[T: ClassTag]() {
  private val handlers = scala.collection.mutable.Map[ClassTag[_ <: T], Buffer[(_ <: T) => Any]]()

  def onEvent[U <: T: ClassTag](handler: (U) => Any): Unit = {
    val callbacks = handlers.getOrElseUpdate(classTag[U], Buffer.empty)
    callbacks += handler
  }

  def fireEvent[U <: T: ClassTag](event: U): Unit = {
    // The caller could have registered a callback on a super class of the event type
    // Ex Event and SpecificEvent extends Event, so we must check for both
    val superClassCallbacks = handlers.getOrElse(classTag[T], Buffer.empty).asInstanceOf[Buffer[T => Any]]
    superClassCallbacks.foreach(_.apply(event))

    val callbacks = handlers.getOrElse(classTag[U], Buffer.empty).asInstanceOf[Buffer[U => Any]]
    callbacks.foreach(_.apply(event))
  }
}

class StubEvents[T: ClassTag]() extends Events[T] {
  val firedEvents = scala.collection.mutable.Buffer[T]()
  override def fireEvent[U <: T: ClassTag](event: U): Unit = {
    super.fireEvent(event)
    firedEvents += event
  }
}

