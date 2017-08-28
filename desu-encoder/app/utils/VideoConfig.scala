package utils

import java.io.File
import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}

import assist.controllers.VideoPathConfig
import net.scalax.mp4.encoder.{CurrentEncode, FFConfig}
import org.slf4j.LoggerFactory
import org.xarcher.nodeWeb.modules.CopyHelper
import play.api.Configuration

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class VideoConfig @Inject() (
                        configure: Configuration
                       ) extends FFConfig with VideoPathConfig with CurrentEncode {
  val logger = LoggerFactory.getLogger(classOf[FFConfig])

  lazy val tempFile = new File(scala.util.Properties.tmpDir)
  lazy val winFfmpegTmpRoot = new File(tempFile, "ffmpegTemp")
  lazy val winFfmpegTempFile = new File(winFfmpegTmpRoot, s"${UUID.randomUUID().toString}")
  lazy val ffmpegRootPath = new File(winFfmpegTempFile, "FormatFactory-4.1.0")
  lazy val ffmpegExeFile = new File(ffmpegRootPath, "FFModules/Encoder/ffmpeg.exe")
  lazy val ffprobeExeFile = new File(ffmpegRootPath, "FFModules/Encoder/ffprobe.exe")
  lazy val mp4boxExeFile = new File(ffmpegRootPath, "FFModules/Encoder/MP4Box/mp4box.exe")

  if (scala.util.Properties.isWin) {
    val oldDate = new Date()
    logger.info(s"复制 ffmpeg 可执行文件到 ${winFfmpegTempFile.getCanonicalPath}")
    Await.result(CopyHelper.copyFromClassPath(List("net", "scalax", "mp4", "encoder", "assets"), winFfmpegTempFile.toPath)(scala.concurrent.ExecutionContext.Implicits.global), Duration.Inf)
    logger.info(s"ffmpeg 复制工作完成，耗时${(new Date().getTime - oldDate.getTime)}毫秒")
    logger.info(s"ffmpeg 可执行文件路径：${ffmpegExeFile.getCanonicalPath}")
    logger.info(s"mp4box 可执行文件路径：${mp4boxExeFile.getCanonicalPath}")
    logger.info(s"请注意定期清理 windows 系统的：${winFfmpegTmpRoot.getCanonicalPath}下所有缓存文件以保证空间充足")
  }

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
    //configure.get[String]("djx314.soft.ffmpeg")
    if (scala.util.Properties.isWin) {
      ffmpegExeFile.getCanonicalPath
    } else {
      "ffmpeg"
    }
  }

  override val ffProbePath = {
    if (scala.util.Properties.isWin) {
      ffprobeExeFile.getCanonicalPath
    } else {
      "ffprobe"
    }
  }

  override val mp4ExePath = {
    //configure.get[String]("djx314.soft.mp4box")
    if (scala.util.Properties.isWin) {
      mp4boxExeFile.getCanonicalPath
    } else {
      "MP4Box"
    }
  }
  /*override val useCanonicalPath = {
    configure.get[Boolean]("djx314.soft.useCanonicalPath")
  }*/
}