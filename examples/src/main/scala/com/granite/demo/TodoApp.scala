package com.granite.demo

import com.bedrock.{ BedrockApp, LocalStorage, StateChange }
import org.scalajs.dom.document
import org.scalajs.dom.raw.HashChangeEvent
import org.scalajs.dom.window
import scala.scalajs.js.Any.fromFunction1
import scala.scalajs.js.JSApp

/** The entry point & main() method of our application */
object TodoApp extends BedrockApp[TodoList] with JSApp {
  private val storageKey = "state"
  private val localStorage = new LocalStorage[TodoList]()
  private val locationController = new LocationController(events)

  override protected val view = new AppView(new TodoAppController(events))
  override protected val initialState = localStorage.load(storageKey).getOrElse(TodoList())

  override def main(): Unit = {
    window.onhashchange = locationController.handleEvent(_: HashChangeEvent)

    events.onEvent { e: StateChange[TodoList] =>
      // when the state changes, we'll persist it to local storage so you
      // don't lose your todolist when you refresh the browser
      localStorage.save(storageKey, e.state)
      println(e.state) // For debugging
    }

    // start the application
    val root = document.getElementById("container")
    renderer.start(root)
  }
}