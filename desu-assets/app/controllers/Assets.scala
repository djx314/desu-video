package assist.controllers

import java.io.File
import java.net.URI
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Future

@Singleton
class Assets @Inject() (assets: CustomAssets,
                        commonAssets: controllers.Assets,
                        components: ControllerComponents,
                        configure: Configuration) extends AbstractController(components) {

  implicit val ec = defaultExecutionContext

  val targetRoot = {
    configure.get[String]("djx314.path.base.target")
  }
  val sourceRoot = {
    configure.get[String]("djx314.path.base.source")
  }

  def target(file1: String) = assets.at(targetRoot, file1)
  def source(file1: String) = assets.at(sourceRoot, file1)

  def at(file1: String) = commonAssets.at("/public", file1)

}