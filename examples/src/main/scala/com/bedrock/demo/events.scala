package com.bedrock.demo

sealed trait TodoEvent

// Editing a todo's name
case class EditStartedEvent(todoId: TodoId) extends TodoEvent
case class EditSubmittedEvent(todoId: TodoId, name: String) extends TodoEvent

// Modifying a todo
case class DeletedEvent(todoId: TodoId) extends TodoEvent
case class CompletionToggledEvent(todoId: TodoId) extends TodoEvent

// Clear all completed todos
case object ClearCompletedEvent extends TodoEvent

// Fired when the user is entering a new todo
case class NewTodoFieldUpdatedEvent(name: String, submitted: Boolean = false) extends TodoEvent