package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import desu.video.akka.config.AppConfig
import desu.video.common.slick.DesuDatabase

import scala.concurrent.Future
import io.circe.syntax._
import desu.video.common.slick.model.Tables._
import desu.video.common.slick.model.Tables.profile.api._
import gd.robot.akka.gdactor.InputRobotActor

import java.awt.Robot
import java.awt.event.KeyEvent

class FileService(appConfig: AppConfig, desuDatabase: DesuDatabase)(implicit system: ActorSystem[Nothing]) {
  implicit val executionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))

  val db = desuDatabase.db

  val actor = system.systemActorOf(InputRobotActor(), "input-robot-actor")

  def callRobot: Future[String] = {
    actor ! InputRobotActor.StartKeyInput(KeyEvent.VK_1)
    Future.successful("成功启动按键")
  }

}
