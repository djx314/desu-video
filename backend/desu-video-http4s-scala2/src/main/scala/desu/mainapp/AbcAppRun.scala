package desu.mainapp

import com.comcast.ip4s._
import cats.effect._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.staticcontent._
import cats._
import cats.implicits._
import fs2.io.net.Network
import org.http4s._

import java.io.ByteArrayInputStream
import java.nio.file.{Files, Paths}
import javax.sound.sampled.{AudioFileFormat, AudioFormat, AudioInputStream, AudioSystem, DataLine, TargetDataLine}
import scala.util.Try

class AudioFormatForApp {
  def getFormat = {
    val sampleRate: Float = 48000 * 2
    // 8,16
    val sampleSizeInBits = 16
    // 1,2
    val channels = 2
    // true,false
    val signed = true
    // true,false
    val bigEndian = false
    // end getAudioFormat
    new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)
  }

  val format: Resource[IO, AudioFormat] = Resource.eval(IO.blocking(getFormat))
}

object AudioFormatForApp {
  def build: AudioFormatForApp = new AudioFormatForApp
}

class AudioResource(val format: AudioFormat, val line: TargetDataLine) {
  val action = IO.blocking {
    val intLong: Int       = 8000000
    val array: Array[Byte] = new Array(intLong)
    line.read(array, 0, array.length)
    val input = new AudioInputStream(new ByteArrayInputStream(array), format, intLong)
    AudioSystem.write(input, AudioFileFormat.Type.WAVE, Paths.get("f:", s"bb${math.random()}.mp3").toFile)
  }
}

class AudioResourceImpl(format: AudioFormat) {

  def startAction(t: TargetDataLine): Try[Boolean] = Try {
    t.open(format)
    t.start()
    true
  }

  def startActionResources(t: TargetDataLine): IO[Boolean] = for {
    tryAction <- IO.blocking(startAction(t))
    a         <- IO.fromTry(tryAction)
  } yield a

  def withDataLine(dataLine: DataLine.Info): Resource[IO, TargetDataLine] = {
    val createAction = IO.blocking(AudioSystem.getLine(dataLine).asInstanceOf[TargetDataLine])
    Resource.fromAutoCloseable(createAction)
  }

  val audioResource: Resource[IO, TargetDataLine] = {
    val dataLineInfo: IO[DataLine.Info] = IO.blocking(new DataLine.Info(classOf[TargetDataLine], format))

    val targetDataLineResourceImpl: Resource[IO, TargetDataLine] = for {
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

object AudioResource {
  def build(implicit format: AudioFormat, line: TargetDataLine): AudioResource = new AudioResource(format = implicitly, line = implicitly)
}

object AudioResourceImpl {
  def build(implicit v: AudioFormat): AudioResourceImpl = new AudioResourceImpl(format = implicitly)
}

object AbcAppRun extends IOApp {
  val resource =
    AudioFormatForApp.build.format.flatMap(implicit t => AudioResourceImpl.build.audioResource.map(implicit v => AudioResource.build))

  val action = resource.use(_.action)

  override def run(args: List[String]): IO[ExitCode] = (action >> action >> action).as(ExitCode.Success)

}
