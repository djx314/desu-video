package net.scalax.mp4.encoder

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{Date, Timer, TimerTask, UUID}
import javax.inject.Singleton
import javax.inject.Inject

import com.google.common.base.Throwables

import scala.concurrent.{ExecutionContext, Future, Promise}
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob.State
import net.bramp.ffmpeg.job.{FFmpegJob, SinglePassFFmpegJob, TwoPassFFmpegJob}
import net.bramp.ffmpeg.progress.{Progress, ProgressListener}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

trait OgvEncoder extends EncoderAbs {

  val logger = LoggerFactory.getLogger(classOf[FFmpegEncoder])

  override val encodeType = "ogvEncoder"

  val fFConfig: FFConfig

  implicit val execContext: ExecutionContext

  lazy val ffmpegExePath = fFConfig.ffmpegExePath

  lazy val mp4BoxExePath = fFConfig.mp4ExePath

  override def encode(videoInfo: String, sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(videoInfo, sourceFiles(0), targetRoot)
  }

  def formatFactoryEncode(videoInfo: String, sourceFile: File, targetRoot: File): Future[List[File]] = {
    targetRoot.mkdirs()
    val tempVideo = new File(targetRoot, "source_video")
    Files.copy(sourceFile.toPath, tempVideo.toPath)

    val targetFile = new File(targetRoot, "target.ogv")

    val ffmpeg = new FFmpeg(s"""$ffmpegExePath""")

    lazy val encodeFuture = Future {

      val builder = new FFmpegBuilder()
        .setInput("source_video")
        .overrideOutputFiles(true) //Filename, or a FFmpegProbeResult
        .addOutput("target.ogv")
        .setFormat("ogg")
        //.setTargetSize(250000)
        //.disableSubtitle()
        .setVideoBitRate(40L * 1000L * 1000L)
        //.setAudioChannels(1)
        .setAudioCodec("vorbis")
        //.setAudioSampleRate(48000)
        //.setAudioBitRate(32768)
        .setVideoCodec("libtheora")
        //.setVideoFrameRate(24, 1)
        //.setVideoResolution(640, 480)
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
        .addExtraArgs("-threads", "16")
        .addExtraArgs("-crf", "10")
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
    }.flatMap(identity)
      .recover {
        case e: Exception =>
          e.printStackTrace
          throw e
      }
      .map { (_: Unit) =>
        List(targetFile)
      }
    //srtEncodeFuture.flatMap(_ => encodeFuture)
    encodeFuture
  }

}

@Singleton
class OgvEncoderImpl @Inject()(ffmpegConfig: FFConfig, mp4Execution: Mp4Execution) extends OgvEncoder {
  override val fFConfig    = ffmpegConfig
  override val execContext = mp4Execution.multiThread
}
