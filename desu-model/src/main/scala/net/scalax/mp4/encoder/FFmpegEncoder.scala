package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.Files
import java.util.{Timer, TimerTask, UUID}
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{Future, Promise}
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.{Progress, ProgressListener}

trait FFmpegEncoder extends EncoderAbs {

  override val encodeType = "ffmpegEncoder"

  val fFConfig: FFConfig

  lazy val ffmpegExePath = new File(fFConfig.ffmpegExePath).getCanonicalPath

  lazy val mp4BoxExePath = new File(fFConfig.mp4ExePath).getCanonicalPath

  override def encode(sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(sourceFiles(0), targetRoot)
  }

  def formatFactoryEncode(sourceFile: File, targetRoot: File): Future[List[File]] = {
    implicit val ec = Execution.multiThread
    targetRoot.mkdirs()
    //val templateFile = new File(targetRoot, "temEncode.mp4")
    val targetFile = new File(targetRoot, "encoded.mp4")
    Future {
      val ffmpeg = new FFmpeg("ffmpeg")
      //val ffprobe = new FFprobe("ffprobe")

      val builder = new FFmpegBuilder().setInput(sourceFile.getCanonicalPath).overrideOutputFiles(true) // Filename, or a FFmpegProbeResult
        .addOutput(targetFile.getCanonicalPath)
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
      new TwoPassFFmpegJob(ffmpeg, builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      }).run()
    }.recover {
      case e: Exception =>
        e.printStackTrace
        throw e
    }.flatMap { (s: Unit) =>
      val mp4BoxCommand = s"""MP4Box -inter 0 ${targetFile.getName}"""
      EncodeHelper.execWithDir(mp4BoxCommand, targetRoot)
    }.map { (_: List[String]) =>
      List(targetFile)
    }
  }

}

@Singleton
class FFmpegEncoderImpl @Inject() (ffmpegConfig: FFConfig) extends FFmpegEncoder {
  override val fFConfig = ffmpegConfig
}