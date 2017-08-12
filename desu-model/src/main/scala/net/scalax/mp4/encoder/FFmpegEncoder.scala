package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{Date, Timer, TimerTask, UUID}
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future, Promise}
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.{Progress, ProgressListener}
import org.slf4j.LoggerFactory

trait FFmpegEncoder extends EncoderAbs {

  val logger = LoggerFactory.getLogger(classOf[FFmpegEncoder])

  override val encodeType = "ffmpegEncoder"

  implicit val execContext: ExecutionContext

  val fFConfig: FFConfig

  lazy val ffmpegExePath = fFConfig.ffmpegExePath/*if (fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.ffmpegExePath).getCanonicalPath
    path
  }
  else
    fFConfig.ffmpegExePath*/

  lazy val mp4BoxExePath = fFConfig.mp4ExePath/*if(fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.mp4ExePath).getCanonicalPath
    path
  }
  else
    fFConfig.mp4ExePath*/

  override def encode(videoInfo: String, sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(videoInfo, sourceFiles(0), targetRoot)
  }

  def formatFactoryEncode(videoInfo: String, sourceFile: File, targetRoot: File): Future[List[File]] = {
    targetRoot.mkdirs()
    val tempVideo = new File(targetRoot, "source_video")
    Files.copy(sourceFile.toPath, tempVideo.toPath)

    val targetFile = new File(targetRoot, "encoded.mp4")
    Future {
      val ffmpeg = new FFmpeg(s"""$ffmpegExePath""")
      //val ffprobe = new FFprobe("ffprobe")

      val builder = new FFmpegBuilder().setInput("source_video").overrideOutputFiles(true) // Filename, or a FFmpegProbeResult
        .addOutput("encoded.mp4")
        .setFormat("mp4")
        //.setTargetSize(250000)
        .disableSubtitle
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

      //val executor = new FFmpegExecutor(ffmpeg, ffprobe)

      //println(scala.collection.JavaConverters.iterableAsScalaIterableConverter(builder.build()).asScala.toList)

      // Run a one-pass encode
      /*executor.createJob(builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      }).run()*/

      // Or run a two-pass encode (which is slower at the cost of better quality)
      /*val twoPass = new TwoPassFFmpegJob(ffmpeg, builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      })

      val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      logger.info(s"于${format.format(new Date())}运行命令:${ffmpeg.path(builder.build())}")

      twoPass.run()*/
      TwoPassFFJob.exec(ffmpeg, builder, targetRoot)
    }.flatMap(identity).recover {
      case e: Exception =>
        e.printStackTrace
        throw e
    }.flatMap { (s: Unit) =>
      EncodeHelper.execWithPath(List(mp4BoxExePath, "-inter", "0", targetFile.getName), targetRoot, { s =>
        logger.info(s)
      }, { s =>
        logger.info(s)
      })
    }.map { (_: Unit) =>
      List(targetFile)
    }
  }

}

@Singleton
class FFmpegEncoderImpl @Inject() (ffmpegConfig: FFConfig, mp4Execution: Mp4Execution) extends FFmpegEncoder {
  override val fFConfig = ffmpegConfig
  override val execContext = mp4Execution.multiThread
}