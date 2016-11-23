package com.granite.demo

import java.util.UUID

/** Id for a todo */
case class TodoId(value: String) extends AnyVal
object TodoId {
  def generate(): TodoId = TodoId(UUID.randomUUID.toString)
}

/** A todo item */
case class Todo(
  id: TodoId,
  name: String,
  completed: Boolean = false,
  editing: Boolean = false)

/** A list of todo items */
case class TodoList(
    todos: Seq[Todo] = Seq.empty,
    newTodoName: Option[String] = None,
    filter: Filter = Filter.All()) {
  def filteredTodos(): Seq[Todo] = todos.filter(Filter.apply(filter))
}

/** A selectable filter on todos (ex all, active, completed)  */
sealed trait Filter
object Filter {
  // Sometimes upicke has trouble auto-generating a serializer for case classes
  // so here we have to help it a bit. Note this is only required if you want to
  // serialize application state to something like local storage (which we do in this example)
  import upickle.default._
  implicit val readWriter: ReadWriter[Filter] =
    macroRW[All] merge macroRW[Active] merge macroRW[Completed]

  // For the same reason above, these have to be case classes, not case objects
  // since upickle doesn't seem to be able to serialize a case obejct
  sealed case class All() extends Filter
  sealed case class Active() extends Filter
  sealed case class Completed() extends Filter

  def apply(filter: Filter): Todo => Boolean = {
    filter match {
      case All() => _ => true
      case Active() => !_.completed
      case Completed() => _.completed
    }
  }
}