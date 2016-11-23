package com.granite.demo

import com.granite.{ AppEvent, Controller, Events, StateChangeRequest }

/**
 * A controller's sole job is to map a UI event into a request to change the application's state (i.e. a StateChangeRequest)
 *
 * The actual mutation of state is handled by the framework and the new state is propagated to the views and/or event listeners
 *
 *  Note that a Controller must specify what events it listens to (TodoEvent) and what state object it requests to change (TodoList)
 *  This allows the framework to enforce that all possible events are handled when pattern matching.
 */
class TodoAppController(override protected val events: Events[AppEvent]) extends Controller[TodoEvent, TodoList] {
  override protected def onEvent(event: TodoEvent): StateChangeRequest[TodoList] = {
    val changeRequest: TodoList => TodoList = event match {
      // New todo field
      case NewTodoFieldUpdatedEvent(name, true) => addTodo(name)
      case NewTodoFieldUpdatedEvent(name, false) => updateNewTodoName(name)

      // Todo items
      case CompletionToggledEvent(id) => toggleCompletion(id)
      case DeletedEvent(id) => deleteTodo(id)
      case EditStartedEvent(id) => editTodoStarted(id)
      case EditSubmittedEvent(id, newName) => editTodoFinished(id, newName)
      case ClearCompletedEvent => clearCompleted(_)
    }

    StateChangeRequest(changeRequest)
  }

  private def addTodo(name: String)(state: TodoList): TodoList = {
    state.copy(newTodoName = None, todos = state.todos :+ Todo(TodoId.generate, name))
  }

  private def toggleCompletion(id: TodoId)(state: TodoList): TodoList = {
    val updated = replaceTodo(id, state.todos) { t => t.copy(completed = !t.completed) }
    state.copy(todos = updated)
  }

  private def clearCompleted(state: TodoList): TodoList = {
    state.copy(todos = state.todos.filterNot(_.completed))
  }

  private def updateNewTodoName(name: String)(state: TodoList): TodoList = {
    state.copy(newTodoName = Some(name))
  }

  private def editTodoStarted(id: TodoId)(state: TodoList): TodoList = {
    val updated = replaceTodo(id, state.todos) { t => t.copy(editing = true) }
    state.copy(todos = updated)
  }

  private def editTodoFinished(id: TodoId, newName: String)(state: TodoList): TodoList = {
    val updated = replaceTodo(id, state.todos) { t => t.copy(name = newName, editing = false) }
    state.copy(todos = updated)
  }

  private def deleteTodo(id: TodoId)(state: TodoList): TodoList = {
    state.copy(todos = state.todos.filter(_.id != id))
  }

  private def replaceTodo(id: TodoId, todos: Seq[Todo])(edit: Todo => Todo): Seq[Todo] = {
    todos.map {
      case t if t.id == id => edit(t)
      case t => t
    }
  }
}