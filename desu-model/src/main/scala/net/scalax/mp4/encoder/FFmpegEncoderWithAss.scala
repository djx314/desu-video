package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{ Date, Timer, TimerTask, UUID }
import javax.inject.Singleton
import javax.inject.Inject

import com.google.common.base.Throwables

import scala.concurrent.{ ExecutionContext, Future, Promise }
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob.State
import net.bramp.ffmpeg.job.{ FFmpegJob, SinglePassFFmpegJob, TwoPassFFmpegJob }
import net.bramp.ffmpeg.progress.{ Progress, ProgressListener }
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

trait FFmpegEncoderWithAss extends EncoderAbs {

  val logger = LoggerFactory.getLogger(classOf[FFmpegEncoder])

  override val encodeType = "ffmpegEncoderWithAss"

  val fFConfig: FFConfig

  implicit val execContext: ExecutionContext

  lazy val ffmpegExePath = fFConfig.ffmpegExePath /*if (fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.ffmpegExePath).getCanonicalPath
    path
  }
  else
    fFConfig.ffmpegExePath*/

  lazy val mp4BoxExePath = fFConfig.mp4ExePath /*if(fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.mp4ExePath).getCanonicalPath
    path
  }
  else
    fFConfig.mp4ExePath*/

  override def encode(videoInfo: String, sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(videoInfo, sourceFiles(0), sourceFiles(1), targetRoot)
  }

  def formatFactoryEncode(videoInfo: String, sourceFile: File, assFile: File, targetRoot: File): Future[List[File]] = {
    //implicit val ec = Execution.multiThread
    targetRoot.mkdirs()
    val tempVideo = new File(targetRoot, "source_video")
    val tempAss = new File(targetRoot, "source_ass")
    Files.copy(sourceFile.toPath, tempVideo.toPath)
    Files.copy(assFile.toPath, tempAss.toPath)

    val targetFile = new File(targetRoot, "encoded.mp4")

    //val sourceSSA = new File(targetRoot, "video.srt")

    val ffmpeg = new FFmpeg(s"""$ffmpegExePath""")

    lazy val encodeFuture = Future {

      val builder = new FFmpegBuilder().setInput("source_video").overrideOutputFiles(true) //Filename, or a FFmpegProbeResult
        .addOutput("encoded.mp4")
        .addExtraArgs("-vf", s"subtitles=source_ass:force_style='fontname=微软雅黑,fontsize=24'")
        .setFormat("mp4")
        //.setTargetSize(250000)
        //.disableSubtitle()
        .setVideoBitRate(1000000L)
        .setAudioChannels(1)
        .setAudioCodec("aac")
        .setAudioSampleRate(48000)
        .setAudioBitRate(32768)
        .setVideoCodec("libx264")
        //.setVideoFrameRate(24, 1)
        //.setVideoResolution(640, 480)
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
        .done

      /*val twoPass = new TwoPassFFmpegJob(ffmpeg, builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      })*/

      //val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      //logger.info(s"于${format.format(new Date())}运行命令:${ffmpeg.path(builder.build())}")
      TwoPassFFJob.exec(ffmpeg, builder, targetRoot)
      //twoPass.run()
    }.flatMap(identity).recover {
      case e: Exception =>
        e.printStackTrace
        throw e
    }.flatMap { (s: Unit) =>
      EncodeHelper.execWithPath(List(mp4BoxExePath, "-isma", targetFile.getName), targetRoot, {
        case Left(s) =>
          logger.info(s)
        case Right(s) =>
          logger.info(s)
      })
    }.map { (_: Unit) =>
      List(targetFile)
    }
    //srtEncodeFuture.flatMap(_ => encodeFuture)
    encodeFuture
  }

}

@Singleton
class FFmpegEncoderWithAssImpl @Inject() (ffmpegConfig: FFConfig, mp4Execution: Mp4Execution) extends FFmpegEncoderWithAss {
  override val fFConfig = ffmpegConfig
  override val execContext = mp4Execution.multiThread
}