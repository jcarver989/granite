

import org.scalajs.dom.document
import org.scalajs.dom.raw.{ Event, Node }
import scalatags.JsDom.all._
import com.bedrock.LocalStorage
import com.bedrock.StateChange
import scala.Vector
import com.bedrock.StateChangeRequest
import com.bedrock.Controller
import com.bedrock.Events
import com.bedrock.AppEvent
import com.bedrock.Component
import scala.scalajs.js.JSApp
import com.bedrock.BedrockApp

/**
 * Scala JS framework example:
 *
 *  Goals:
 *  - Borrow the "good" ideas from React/Elm, 1 way data flow (events up, data/state down)
 *  - Abandon the "bad" things: lack of type safety, not enough structure (react is just the view... => fragmentation)
 *  - Really easy to test => aka  things should be "pure" where possible.
 *  - With view templates you have to make a tradeoff between "developer friendly" & "designer friendly" - want the framework to give you the option to choose that tradeoff
 *
 *  Non Goals:
 *  - Virtual DOM (currently think we don't really need it 99% of the time, DOM is fast enough)
 *  -
 */

/** A simple todo Application */

/** Step: 1 Define your application state */
case class DemoState(todoItems: Seq[Todo] = Vector())
case class Todo(name: String)

/** Step: 2 Our "view" needs to send an event when we  want to add/remove a Todo, so let's create those now */
sealed trait TodoEvent extends AppEvent
sealed case class RowAddedEvent() extends TodoEvent
sealed case class RowRemovedEvent(index: Int) extends TodoEvent

/** Step 3: When a UI event happens, we want to update our DemoState's list of todos, we'll write pure functions to do that */
case class TodoEventHandler(protected override val events: Events[AppEvent]) extends Controller[TodoEvent, DemoState] {
  override def onEvent(event: TodoEvent): StateChangeRequest[DemoState] = {
    val newState: DemoState => DemoState = event match {
      case e: RowAddedEvent => addRow
      case e: RowRemovedEvent => removeRow(e.index)
    }

    StateChangeRequest(newState)
  }

  private def addRow(state: DemoState): DemoState = {
    state.copy(todoItems = state.todoItems :+ Todo(""))
  }

  private def removeRow(index: Int)(state: DemoState): DemoState = {
    val updatedTodos = state.todoItems.zipWithIndex.filterNot { case (t, i) => i == index }.map(_._1)
    state.copy(todoItems = updatedTodos)
  }
}

/** Step: 4 Define your view as a "pure" function of DemoState */
class AppView(eventHandler: Controller[TodoEvent, DemoState]) extends Component[DemoState] {

  // our "pure" function of State => DOM nodes
  override def render(state: DemoState): Node = {
    val rows = div(state.todoItems.zipWithIndex.map { case (todo, i) => row(todo, i) }).render

    val t = table(cls := "green line-items",
      thead(
        tr(
          th(span(cls := "title"), "Todos"),
          th(addLink)))
    ).render

    div(t, table(cls := "line-items", rows)).render
  }

  // render a single todo
  private def row(todo: Todo, index: Int): Node = {
    val removeLink = a(
      cls := "remove",
      i(cls := "fi-x"),
      onclick := { e: Event => e.preventDefault(); removeRow(index) })

    tr(
      td(nameField(todo, index)),
      td(removeLink)).render
  }

  // render the "add link" at the top
  private def addLink(): Node = {
    a(
      cls := "small hollow button add-item",
      "Add ",
      i(cls := "fi-plus"),
      onclick := { e: Event =>
        e.preventDefault()
        addRow()
      }).render
  }

  // render the name input
  private def nameField(todo: Todo, index: Int): Node = {
    input(
      `type` := "text",
      placeholder := "Name",
      cls := "name",
      value := todo.name
    ).render
  }

  private def addRow(): Unit = {
    eventHandler.handleEvent(RowAddedEvent())
  }

  private def removeRow(index: Int): Unit = {
    eventHandler.handleEvent(RowRemovedEvent(index))
  }
}

/** Our entry point (main) method that is called via JS */
object Demo extends BedrockApp[DemoState] with JSApp {
  private val dbKey = "demo"
  private val db = new LocalStorage[DemoState]()
  override protected val view = new AppView(new TodoEventHandler(events))
  override protected val initialState = db.load(dbKey).getOrElse(DemoState())

  override def main() {
    val root = document.getElementById("container")
    renderer.start(root)
    events.onEvent { e: StateChange[DemoState] => db.save(dbKey, e.state) }
  }
}

