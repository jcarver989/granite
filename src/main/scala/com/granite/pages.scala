package com.granite

import org.scalajs.dom.raw.{ Node, PopStateEvent }
import org.scalajs.dom.window
import org.scalajs.dom.window.history
import scala.scalajs.js
import scala.scalajs.js.Any.fromFunction1
import scala.scalajs.js.annotation.ScalaJSDefined
import org.scalajs.dom.raw.Element

/** Typeclass for Applications that want to have page routing */
trait HasPages[T] {
  def getSelectedPage(state: T): String
  def selectPage(page: String, state: T): T
}

/** Handles pages/routes within the Application. */
class PageRouter[T: HasPages](events: Events[AppEvent], routes: (String, Component[T])*) extends Component[T] {
  private val ev = implicitly[HasPages[T]]
  private val routeMap = routes.toMap

  window.addEventListener("popstate", { e: PopStateEvent =>
    val url = e.state.asInstanceOf[PageHistory].url
    events.fireEvent(StateChangeRequest(ev.selectPage(url, _: T)))
  })

  override def render(state: T): Element = {
    val currentUrl = ev.getSelectedPage(state)
    val windowUrl = window.location.href.split("/").last
    if (currentUrl != windowUrl) {
      history.pushState(new PageHistory(currentUrl), "", currentUrl)
    }

    routeMap(ev.getSelectedPage(state)).renderNode(state)
  }
}

/** A custom entry in the HTML5 History API */
@ScalaJSDefined
class PageHistory(val url: String) extends js.Object
