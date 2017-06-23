package net.scalax.mp4.encoder

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{Date, Timer, TimerTask, UUID}
import javax.inject.Singleton
import javax.inject.Inject

import scala.concurrent.{Future, Promise}
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.{FFmpegJob, SinglePassFFmpegJob, TwoPassFFmpegJob}
import net.bramp.ffmpeg.progress.{Progress, ProgressListener}
import org.slf4j.LoggerFactory

trait FFmpegEncoderWithAss extends EncoderAbs {

  val logger = LoggerFactory.getLogger(classOf[FFmpegEncoder])

  override val encodeType = "ffmpegEncoderWithAss"

  val fFConfig: FFConfig

  lazy val ffmpegExePath = if (fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.ffmpegExePath).getCanonicalPath
    path
  }
  else
    fFConfig.ffmpegExePath

  lazy val mp4BoxExePath = if(fFConfig.useCanonicalPath) {
    val path = new File(fFConfig.mp4ExePath).getCanonicalPath
    path
  }
  else
    fFConfig.mp4ExePath

  override def encode(sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]] = {
    formatFactoryEncode(sourceFiles(0), sourceFiles(1), targetRoot)
  }

  def formatFactoryEncode(sourceFile: File, assFile: File, targetRoot: File): Future[List[File]] = {
    implicit val ec = Execution.multiThread
    targetRoot.mkdirs()
    val tempFile = new File(targetRoot, "temp.mp4")
    val targetFile = new File(targetRoot, "encoded.mp4")

    val sourceSSA = new File(targetRoot, "video.ass")
    Files.copy(assFile.toPath, sourceSSA.toPath)
    Future {
      val ffmpeg = new FFmpeg(s"""$ffmpegExePath""")

      val builder = new FFmpegBuilder().setInput(sourceFile.getCanonicalPath).overrideOutputFiles(true) // Filename, or a FFmpegProbeResult
        .addOutput(tempFile.getCanonicalPath)
        .addExtraArgs("-vf", s"""ass=${sourceSSA.getCanonicalPath}""")
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

      /*val twoPass = new TwoPassFFmpegJob(ffmpeg, builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      })*/
      val twoPass = new SinglePassFFmpegJob(ffmpeg, builder, new ProgressListener() {
        override def progress(progress: Progress): Unit = {
          println(progress)
        }
      })

      val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      logger.info(s"于${format.format(new Date())}运行命令:${ffmpeg.path(builder.build())}")

      twoPass.run()
    }.recover {
      case e: Exception =>
        e.printStackTrace
        throw e
    }.flatMap { (s: Unit) =>
      //val mp4BoxCommand = s"""${mp4BoxExePath} -inter 0 ${targetFile.getName}"""
      EncodeHelper.execWithDir(List(mp4BoxExePath, "-inter", "0", tempFile.getName), targetRoot)
    }/*.flatMap { s =>
      //mp4box -ipod -add 1.srt:lang=zh:name=Caption:hdlr=sbtl:font="微软雅黑":size=22 -new 1.srt.mp4
      //E:\xx\tools\mp4box -ipod -add e:\xx\xx.mp4 -add e:\xx\xx.ass:lang=zh:name=aaa:group=2:hdlr=sbtl:font="微软雅黑":size=22 -new e:\xx\a.mp4
      EncodeHelper.execWithDir(List(mp4BoxExePath, "-add", tempFile.getName, "-ipod", "-add",
        sourceSSA.getName + """:lang=zh:name=aaa:group=2:hdlr=sbtl:font="微软雅黑":size=22""",
      "-new",
        targetFile.getName), targetRoot)
    }*/.map { (_: List[String]) =>
      List(tempFile)
    }
  }

}

@Singleton
class FFmpegEncoderWithAssImpl @Inject() (ffmpegConfig: FFConfig) extends FFmpegEncoderWithAss {
  override val fFConfig = ffmpegConfig
}