package gd.robot.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}

class AppConfig(system: ActorSystem[Nothing])

object AppConfig {
  val defaultDispatcherName = "desu-dispatcher"
  val gdSelector            = DispatcherSelector.fromConfig(defaultDispatcherName)
}
