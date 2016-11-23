package com.granite.demo

import com.granite.{ Component, Controller }
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{ Event, KeyboardEvent, Node }
import scalatags.JsDom.all._

/**
 * Components are intended to be reusable views across projects/apps.
 *
 *  They render themselves as a pure function of the application's current state and
 *  emit events when things need to change.
 *
 *  These events are handled by the Controller which translates the UI event into a request to change the
 *  application's state.
 *
 *  To make Components easily reusable across applications they should depend on state that is not app-specific, ex
 *  Seq[Foo] vs SpecificApplicationState(foos: Seq[Foo], otherStuff: ...).
 *
 *  Since controllers are coupled to the application state, they are always app-specific, so it's a best practice to
 *  pass the controller to the view as a dependency - that way other apps can use the component simply by passing in their
 *  own controller(s)
 *
 */
class AppView(controller: Controller[TodoEvent, TodoList]) extends Component[TodoList] {
  import controller.handleEvent // really just to save typing

  override def render(state: TodoList): Node = {
    div(cls := "todoapp",
      header(state),
      if (state.todos.isEmpty) div().render else todoList(state.filteredTodos),
      if (state.todos.isEmpty) div().render else footer(state.todos, state.filter)
    ).render
  }

  private def header(state: TodoList): Node = {
    val onKeyUp = { e: KeyboardEvent =>
      val value = e.target.asInstanceOf[Input].value // safe cast since we know this handler is only bound to the input below
      handleEvent(NewTodoFieldUpdatedEvent(value, e.keyCode == KeyCode.Enter))
    }

    div(
      cls := "header",
      h1("todos"),
      input(
        cls := "new-todo",
        placeholder := "What needs to be done?",
        value := state.newTodoName.getOrElse(""),
        onkeyup := onKeyUp,
        autofocus)
    ).render
  }

  private def todoList(todos: Seq[Todo]): Node = {
    div(cls := "main",
      input(cls := "toggle-all", `type` := "checkbox"),
      label(`for` := "toggle-all", "Mark all as complete"),
      ul(
        cls := "todo-list",
        todos.map(todoItem)
      )
    ).render
  }

  private def todoItem(todo: Todo): Node = {
    val onKeyUp = { e: KeyboardEvent =>
      if (e.keyCode == KeyCode.Enter) {
        val value = e.target.asInstanceOf[Input].value // safe cast since we know this handler is only bound to the input below
        handleEvent(EditSubmittedEvent(todo.id, value))
      }
    }
    val cssClass = if (todo.completed) "completed" else if (todo.editing) "editing" else ""
    li(
      cls := cssClass,
      div(cls := "view",
        input(
          cls := "toggle",
          `type` := "checkbox",
          if (todo.completed) checked,
          onclick := { e: Event => handleEvent(CompletionToggledEvent(todo.id)) }),

        label(todo.name),
        button(cls := "destroy", onclick := { e: Event => handleEvent(DeletedEvent(todo.id)) })
      ),
      input(cls := "edit", `value` := todo.name, autofocus, onkeyup := onKeyUp),
      ondblclick := { e: Event => handleEvent(EditStartedEvent(todo.id)) }
    ).render
  }

  private def footer(todos: Seq[Todo], filter: Filter): Node = {
    val remainingItems = todos.filterNot(_.completed).size
    val pluralModifier = if (remainingItems > 1) "s" else ""
    div(cls := "footer",
      span(cls := "todo-count",
        strong(remainingItems + " "),
        s"item${pluralModifier} left"),

      ul(cls := "filters",
        li(a(href := "#/", "All", if (filter == Filter.All()) cls := "selected")),
        li(a(href := "#/active", "Active", if (filter == Filter.Active()) cls := "selected")),
        li(a(href := "#/completed", "Completed", if (filter == Filter.Completed()) cls := "selected"))
      ),

      button(
        cls := "clear-completed",
        "Clear completed",
        onclick := { e: Event => handleEvent(ClearCompletedEvent) })
    ).render
  }
}