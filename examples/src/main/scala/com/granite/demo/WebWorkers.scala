package com.granite.demo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSApp

import com.granite.webworkers.WebWorkerInstance
import com.granite.webworkers.WebWorkerTask
import com.granite.webworkers.WorkerPool

import upickle.default._

// Messages the workers can send
sealed trait WorkerTask extends WebWorkerTask
case class SayHello(id: String, result: Option[String] = None) extends WorkerTask

// The code that runs on a worker
object WorkerInstance extends WebWorkerInstance[WorkerTask] {
  override val scriptDependencies = Vector("target/scala-2.11/granite-example-fastopt.js")
  override def onMessage(msg: WorkerTask): WorkerTask = {
    msg match {
      case task: SayHello => task.copy(result = Some("Hello there!"))
    }
  }
}

// The code that runs on the main thread
object WebWorkersApp extends JSApp {
  val pool = new WorkerPool(WorkerInstance, 4)
  override def main(): Unit = {
    pool.run(SayHello(nextId)).foreach { result =>
      println(result)
    }
  }

  private def nextId(): String = {
    java.util.UUID.randomUUID.toString
  }
}