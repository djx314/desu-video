package assist.controllers

import javax.inject.{Inject, Singleton}

import net.scalax.mp4.encoder._

@Singleton
class VideoEncodersImpl @Inject()(fFmpegEncoder: FFmpegEncoder, fFmpegEncoderWithAss: FFmpegEncoderWithAss, ogvEncoder: OgvEncoder) extends VideoEncoders {

  override val encoders = fFmpegEncoder :: fFmpegEncoderWithAss :: ogvEncoder :: Nil

}

trait VideoEncoders {

  val encoders: List[EncoderAbs]

}
