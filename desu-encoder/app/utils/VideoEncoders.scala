package assist.controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.encoder.{EncoderAbs, FormatFactoryEncoder}
import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class VideoEncodersImpl @Inject() (formatFactoryEncoder: FormatFactoryEncoder) extends VideoEncoders {

  override val encoders = formatFactoryEncoder :: Nil

}

trait VideoEncoders {

  val encoders: List[EncoderAbs]

}