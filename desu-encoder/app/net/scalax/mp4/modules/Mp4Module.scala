package net.scalax.mp4.modules

import assist.controllers.{VideoEncoders, VideoEncodersImpl}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.scalax.mp4.encoder.{FFConfig, FormatFactoryEncoder, FormatFactoryEncoderImpl}
import utils.VideoConfig

class Mp4Module extends AbstractModule {

  def configure() = {
    bind(classOf[VideoEncoders])
      .to(classOf[VideoEncodersImpl])

    bind(classOf[FormatFactoryEncoder])
      .to(classOf[FormatFactoryEncoderImpl])

    bind(classOf[FFConfig])
      .to(classOf[VideoConfig])
  }

}