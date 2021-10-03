package gd.robot.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter

import com.typesafe.config.ConfigFactory

import desu.video.akka.model.FileNotConfirmException

import java.nio.file.{Files, Path, Paths}

import scala.concurrent.Future

class AppConfig(system: ActorSystem[Nothing])

object AppConfig {
  val defaultDispatcherName = "desu-dispatcher"
  val gdSelector            = DispatcherSelector.fromConfig(defaultDispatcherName)
}
