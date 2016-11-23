package com.granite.demo

import com.bedrock.{ AppEvent, Controller, Events, StateChangeRequest }
import org.scalajs.dom.raw.HashChangeEvent

// asks the application state to change the todo-filter when the browser's location changes
class LocationController(protected val events: Events[AppEvent]) extends Controller[HashChangeEvent, TodoList] {
  override def onEvent(e: HashChangeEvent): StateChangeRequest[TodoList] = {
    val request: TodoList => TodoList = e.newURL match {
      case u if u.contains("#/active") => _.copy(filter = Filter.Active())
      case u if u.contains("#/completed") => _.copy(filter = Filter.Completed())
      case _ => _.copy(filter = Filter.All())
    }

    StateChangeRequest(request)
  }
}