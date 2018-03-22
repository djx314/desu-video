package assist.controllers

import javax.inject.{ Inject, Singleton }

import archer.controllers.CommonController
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.mvc.ControllerComponents

import scala.concurrent.Future

@Singleton
class Assets @Inject() (
  assets: CustomAssets,
  configure: Configuration,
  controllerComponents: ControllerComponents) extends CommonController(controllerComponents) {
  implicit def ec = defaultExecutionContext
  /*val targetRoot = {
    configure.get[String]("djx314.path.base.target")
  }
  val sourceRoot = {
    configure.get[String]("djx314.path.base.source")
  }

  def target(file1: String) = assets.at(targetRoot, file1)
  def source(file1: String) = assets.at(sourceRoot, file1)*/

}