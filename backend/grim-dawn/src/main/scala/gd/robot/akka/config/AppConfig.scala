package gd.robot.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter

import com.typesafe.config.ConfigFactory

import desu.video.akka.model.FileNotConfirmException

import java.nio.file.{Files, Path, Paths}

import scala.concurrent.Future

class AppConfig(system: ActorSystem[Nothing]) {
  val executionContext      = system.dispatchers.lookup(AppConfig.gdSelector)
  val blockExecutionContext = system.dispatchers.lookup(DispatcherSelector.blocking())

  val dirPath = ConfigFactory.load().getString("desu.video.file.rootPath")
}

object AppConfig {
  val defaultDispatcherName1 = "desu-dispatcher"
  val gdSelector             = DispatcherSelector.fromConfig(defaultDispatcherName1)
}
