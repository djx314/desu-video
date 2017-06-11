package utils

import java.io.File
import java.net.URI
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{DateInfo, RequestInfo}
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import play.api.libs.circe.Circe
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future

@Singleton
class VideoConfig @Inject() (
                        configure: Configuration
                       ) {

  val ffmpegRoot = {
    configure.get[String]("djx314.path.base.ffmpeg")
  }
  val assetsPrefix = {
    configure.get[String]("djx314.url.server.asset")
  }

}