package utils

<<<<<<< HEAD
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

  val ffmpegRoot: String = {
    configure.get[String]("djx314.path.base.ffmpeg")
  }
  /*val assetsPrefix: String = {
    configure.get[String]("djx314.url.server.asset")
  }*/
  val ffmpegSoftPath: String = {
    configure.get[String]("djx314.soft.ffmpeg")
=======
import javax.inject.{Inject, Singleton}

import assist.controllers.VideoPathConfig
import net.scalax.mp4.encoder.{CurrentEncode, FFConfig}
import play.api.Configuration

@Singleton
class VideoConfig @Inject() (
                        configure: Configuration
                       ) extends FFConfig with VideoPathConfig with CurrentEncode {

  override val uploadRoot: String = {
    configure.get[String]("djx314.path.base.upload.root")
  }
  /*val assetsPrefix: String = {
    configure.get[String]("djx314.url.server.asset")
  }*/
  /*val ffmpegSoftPath: String = {
    configure.get[String]("djx314.soft.ffmpeg")
  }*/
  override val ffmpegExePath = {
    configure.get[String]("djx314.soft.ffmpeg")
  }
  override val mp4ExePath = {
    configure.get[String]("djx314.soft.mp4box")
  }

  override val useCanonicalPath = {
    configure.get[Boolean]("djx314.soft.useCanonicalPath")
>>>>>>> branch 'master' of https://djx314:xingxing314@git.coding.net/djx314/desu-encoder.git
  }

}