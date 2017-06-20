package utils

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

}