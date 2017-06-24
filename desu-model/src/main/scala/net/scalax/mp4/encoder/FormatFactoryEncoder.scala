package net.scalax.mp4.encoder

import java.io.File
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

trait FormatFactoryEncoder extends EncoderAbs {

  override val encodeType = "FormatFactoryEncoder"

  val fFConfig: FFConfig

  implicit val execContext: ExecutionContext

  lazy val ffmpegExePath = new File(fFConfig.ffmpegExePath).getCanonicalPath

  lazy val mp4BoxExePath = new File(fFConfig.mp4ExePath).getCanonicalPath

  override def encode(videoInfo: String, sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(videoInfo, sourceFiles(0), targetRoot)
  }

  def formatFactoryEncode(ideoInfo: String, sourceFile: File, targetRoot: File): Future[List[File]] = {

    val targetFile = new File(targetRoot, "encoded.mp4")
    val command = s""" "${ffmpegExePath}" "Custom" "customMp4" "${sourceFile.getCanonicalPath}" "${targetFile.getCanonicalPath}" """
    val mp4BoxCommand = s""" "${mp4BoxExePath}" -inter 0 "${targetFile.getCanonicalPath}" """

    val execFuture = EncodeHelper.execCommand(command).flatMap { _ =>
      EncodeHelper.windowsWaitTargetFileFinishedEncode(targetFile)
    }.flatMap { isFinished =>
      if (isFinished) {
        EncodeHelper.execCommand(mp4BoxCommand)
      } else {
        throw new Exception("windows 系统转换出现异常")
      }
    }.map(_ => targetFile :: Nil)

    execFuture
  }

}

@Singleton
class FormatFactoryEncoderImpl @Inject() (ffmpegConfig: FFConfig, mp4Execution: Mp4Execution) extends FormatFactoryEncoder {
  override val fFConfig = ffmpegConfig
  override val execContext = mp4Execution.multiThread
}