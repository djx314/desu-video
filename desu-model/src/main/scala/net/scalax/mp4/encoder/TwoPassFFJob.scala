package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.{ DirectoryStream, Files, Path, Paths }
import java.text.SimpleDateFormat
import java.util.{ Date, Timer, TimerTask, UUID }
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future, Promise }
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.FFmpegBuilder.Verbosity
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.{ Progress, ProgressListener }
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object TwoPassFFJob {

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
    val pass1ExecList = ffmpeg.path(builder.setPass(1).setVerbosity(Verbosity.INFO).build()).asScala.toList
    lazy val pass1Exec = EncodeHelper.execWithPath(pass1ExecList, root, {
      case Left(s) =>
        onePassLogger.info(s)
      case Right(s) =>
        onePassLogger.info(s)
    })

    val pass2ExecList = ffmpeg.path(builder.setPass(2).setVerbosity(Verbosity.INFO).build()).asScala.toList
    lazy val pass2Exec = EncodeHelper.execWithPath(pass2ExecList, root, {
      case Left(s) =>
        twoPassLogger.info(s)
      case Right(s) =>
        twoPassLogger.info(s)
    }).map { (_: Unit) =>
      deletePassLog(Paths.get(root.toURI), passlogPrefix)
    }

    pass1Exec.flatMap { (_: Unit) => pass2Exec }
  }

}