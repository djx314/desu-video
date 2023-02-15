package desu.mainapp

import cats.effect.{IO => _, _}
import cats.effect.implicits._
import cats._
import cats.implicits._
import fs2._

import scala.util.Try

import javax.sound.sampled.{AudioFileFormat, AudioFormat, AudioInputStream, AudioSystem, DataLine, Line, TargetDataLine}

class AudioFormatForApp {
  private def getFormatImpl = {
    val sampleRate: Float = 48000
    // 8,16
    val sampleSizeInBits = 16
    // 1,2
    val channels = 2
    // true,false
    val signed = true
    // true,false
    val bigEndian = false
    val frameSize = 20
    val frameRate = 48000
    // end getAudioFormat
    new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)
    /*new AudioFormat(
      AudioFormat.Encoding.PCM_SIGNED,
      sampleRate,
      sampleSizeInBits,
      channels,
      frameSize,
      frameRate,
      bigEndian
    )*/
  }

  def format[F[_]: Sync]: Resource[F, AudioFormat] = Resource.eval(Sync[F].blocking(getFormatImpl))
}

object AudioFormatForApp {
  def build: AudioFormatForApp = new AudioFormatForApp
}

class AudioResource(line: TargetDataLine) {

  def resource[F[_]: Sync] = {
    def r = new AudioInputStream(line)
    Resource.fromAutoCloseable(Sync[F].blocking(r))
  }

}
object AudioResource {
  def build(implicit line: TargetDataLine): AudioResource = new AudioResource(line = implicitly)
}

class AAb(input: AudioInputStream) {
  def action[F[_]: Async]: Stream[F, Byte] = {

    fs2.io.readOutputStream(100)(out => Sync[F].blocking(AudioSystem.write(input, AudioFileFormat.Type.WAVE, out)))
  }

}

object AAb {
  def build(implicit input: AudioInputStream): AAb = new AAb(implicitly)
}

class AudioResourceImpl(format: AudioFormat) {

  private def startAction(t: TargetDataLine): Try[Boolean] = Try {
    t.open(format)
    t.start()
    true
  }

  private def startActionResources[F[_]: Sync](t: TargetDataLine): F[Boolean] = for {
    tryAction <- Sync[F].blocking(startAction(t))
    a         <- ApplicativeError[F, Throwable].fromTry(tryAction)
  } yield a

  private def withDataLine[F[_]: Sync](dataLine: Line.Info): Resource[F, TargetDataLine] = {
    val createAction = Sync[F].blocking(AudioSystem.getLine(dataLine).asInstanceOf[TargetDataLine])
    Resource.fromAutoCloseable(createAction)
  }

  def audioResource[F[_]: Sync]: Resource[F, TargetDataLine] = {
    val dataLineInfo: F[Line.Info] = Sync[F].blocking(new DataLine.Info(classOf[TargetDataLine], format))

    val targetDataLineResourceImpl: Resource[F, TargetDataLine] = for {
      eachData <- Resource.eval(dataLineInfo)
      r2       <- withDataLine(eachData)
    } yield r2

    for {
      t <- targetDataLineResourceImpl
      u <- Resource.eval(startActionResources(t))
    } yield {
      u: Boolean
      t
    }

  }
}

object AudioResourceImpl {
  def build(implicit v: AudioFormat): AudioResourceImpl = new AudioResourceImpl(format = implicitly)
}

object AbcAppRun {
  def resource[F[_]: Sync]: Resource[F, AAb] =
    AudioFormatForApp.build.format.flatMap(implicit t =>
      AudioResourceImpl.build.audioResource.flatMap(implicit v => AudioResource.build.resource.map(implicit input => AAb.build))
    )
}
