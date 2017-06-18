package net.scalax.mp4.encoder

import java.util.concurrent.Executors

import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }

object Execution {

  val multiThread: ExecutionContextExecutor = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

}