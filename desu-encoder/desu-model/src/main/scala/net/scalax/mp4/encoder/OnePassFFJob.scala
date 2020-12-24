package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.{DirectoryStream, Files, Path, Paths}
import java.text.SimpleDateFormat
import java.util.{Date, Timer, TimerTask, UUID}
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future, Promise}
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.FFmpegBuilder.Verbosity
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.{Progress, ProgressListener}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object OnePassFFJob {

  val onePassLogger = LoggerFactory.getLogger("OnePassFFJob")
  val twoPassLogger = LoggerFactory.getLogger("TwoPassFFJob")

  def deletePassLog(path: Path, passlogPrefix: String)(implicit ec: ExecutionContext): Unit = {
    //val cwd = Paths.get("")
    val stream = Files.newDirectoryStream(path, passlogPrefix + "*.log*")
    try {
      import scala.collection.JavaConverters._
      for (p <- stream.asScala.toList) {
        Files.deleteIfExists(p)
      }
    } finally if (stream != null) stream.close()
  }

  def exec(ffmpeg: FFmpeg, builder: FFmpegBuilder, root: File)(implicit ec: ExecutionContext): Future[Unit] = {
    val passlogPrefix = UUID.randomUUID.toString
    builder.setPassPrefix(passlogPrefix)
    val pass1ExecList = ffmpeg.path(builder.setVerbosity(Verbosity.INFO).build()).asScala.toList
    lazy val pass1Exec = EncodeHelper.execWithPath(pass1ExecList, root, {
      case Left(s) =>
        onePassLogger.info(s)
      case Right(s) =>
        onePassLogger.info(s)
    })

    pass1Exec
  }

}
