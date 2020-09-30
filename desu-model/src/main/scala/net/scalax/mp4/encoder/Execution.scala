package net.scalax.mp4.encoder

import java.util.concurrent.Executors
import javax.inject.{Inject, Singleton}

import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

@Singleton
class Mp4Execution @Inject() (applicationLifecycle: ApplicationLifecycle) {

  val multiThread: ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

  applicationLifecycle.addStopHook { () =>
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      multiThread.shutdownNow()
      println("mp4 转码线程池已关闭")
    }
  }

}
