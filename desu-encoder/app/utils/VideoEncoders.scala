package assist.controllers

import javax.inject.{Inject, Singleton}

import net.scalax.mp4.encoder.{EncoderAbs, FFmpegEncoder, FFmpegEncoderWithAss, FormatFactoryEncoder}

@Singleton
class VideoEncodersImpl @Inject() (formatFactoryEncoder: FormatFactoryEncoder, fFmpegEncoder: FFmpegEncoder, fFmpegEncoderWithAss: FFmpegEncoderWithAss) extends VideoEncoders {

  override val encoders = formatFactoryEncoder :: fFmpegEncoder :: fFmpegEncoderWithAss :: Nil

}

trait VideoEncoders {

  val encoders: List[EncoderAbs]

}