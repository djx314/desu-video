package assist.controllers

import javax.inject.{Inject, Singleton}

import net.scalax.mp4.encoder._

@Singleton
class VideoEncodersImpl @Inject() (
                                    formatFactoryEncoder: FormatFactoryEncoder,
                                   fFmpegEncoder: FFmpegEncoder,
                                   fFmpegEncoderWithAss: FFmpegEncoderWithAss,
                                    ogvEncoder: OgvEncoder
                                  ) extends VideoEncoders {

  override val encoders = formatFactoryEncoder :: fFmpegEncoder :: fFmpegEncoderWithAss :: ogvEncoder :: Nil

}

trait VideoEncoders {

  val encoders: List[EncoderAbs]

}