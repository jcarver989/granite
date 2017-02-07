package com.granite

import scala.concurrent.ExecutionContextExecutor

/**
 * For testing purposes only - forces futures to execute in a non-async manner.
 *
 *  Scala-test does have classes to help with testing Futures, however it requires access
 *  to the Future you want to wait on. When testing rendering code, the Futures are not exposed
 *  so we must force them to block instead
 */
object BlockingExecutionContext {
  implicit val executor: ExecutionContextExecutor = ExecutionContext

  object ExecutionContext extends ExecutionContextExecutor {
    def execute(runnable: Runnable): Unit = {
      try {
        runnable.run()
      } catch {
        case t: Throwable => reportFailure(t)
      }
    }

    def reportFailure(t: Throwable): Unit = t.printStackTrace()
  }
}