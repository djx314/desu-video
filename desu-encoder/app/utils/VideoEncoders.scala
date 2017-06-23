package assist.controllers

import javax.inject.{Inject, Singleton}

import net.scalax.mp4.encoder.{EncoderAbs, FFmpegEncoder, FormatFactoryEncoder}

@Singleton
class VideoEncodersImpl @Inject() (formatFactoryEncoder: FormatFactoryEncoder, fFmpegEncoder: FFmpegEncoder) extends VideoEncoders {

  override val encoders = formatFactoryEncoder :: fFmpegEncoder :: Nil

}

trait VideoEncoders {

  val encoders: List[EncoderAbs]

}