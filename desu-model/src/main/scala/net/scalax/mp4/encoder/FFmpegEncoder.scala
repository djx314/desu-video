package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{ Date, Timer, TimerTask, UUID }
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future, Promise }
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.{ Progress, ProgressListener }
import org.slf4j.LoggerFactory

trait FFmpegEncoder extends EncoderAbs {

  val logger = LoggerFactory.getLogger(classOf[FFmpegEncoder])

  override val encodeType = "ffmpegEncoder"

  implicit val execContext: ExecutionContext

  val fFConfig: FFConfig

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
  lazy val ffProbeExePath = fFConfig.ffProbePath

  override def encode(videoInfo: String, sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(videoInfo, sourceFiles(0), targetRoot)
  }

  def formatFactoryEncode(videoInfo: String, sourceFile: File, targetRoot: File): Future[List[File]] = {
    targetRoot.mkdirs()
    val tempVideo = new File(targetRoot, "video_01.mp4")
    Files.copy(sourceFile.toPath, tempVideo.toPath)
    val targetSize = Files.size(tempVideo.toPath)

    val targetFileName = "video_target.mp4"
    val targetFile = new File(targetRoot, targetFileName)
    val ffprobe = new FFprobe(ffProbeExePath)

    Future {
      val ffmpeg = new FFmpeg(s"""$ffmpegExePath""")
      //val ffprobe = new FFprobe("ffprobe")

      val builder = new FFmpegBuilder().addInput(ffprobe.probe(tempVideo.getCanonicalPath)).overrideOutputFiles(true) // Filename, or a FFmpegProbeResult
        //.addOutput(targetFile.getCanonicalPath)
        .addOutput(targetFileName)
        .setTargetSize(targetSize)
        .setFormat("mp4")
        .disableSubtitle
        //.setVideoBitRate(1000000L)
        //.setVideoBitRate(40L * 1000L * 1000L)
        .setAudioChannels(1)
        .setAudioCodec("aac")
        .setAudioSampleRate(48000)
        .setAudioBitRate(32768)
        .setVideoCodec("libx264")
        //.setVideoFrameRate(24, 1)
        //.setVideoResolution(640, 480)
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
        .addExtraArgs("-crf", "10")
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
      EncodeHelper.execWithPath(List(mp4BoxExePath, "-isma", targetFileName), targetRoot, {
        case Left(s) =>
          logger.info(s)
        case Right(s) =>
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