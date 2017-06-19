package net.scalax.mp4.modules

import assist.controllers._
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.scalax.mp4.encoder._
import utils.VideoConfig

class Mp4Module extends AbstractModule {

  def configure() = {
    bind(classOf[VideoEncoders])
      .to(classOf[VideoEncodersImpl])

    bind(classOf[FormatFactoryEncoder])
      .to(classOf[FormatFactoryEncoderImpl])

    bind(classOf[FFConfig])
      .to(classOf[VideoConfig])

    bind(classOf[VideoPathConfig])
      .to(classOf[VideoConfig])

    bind(classOf[FilesReply])
      .to(classOf[FilesReplyImpl])

    bind(classOf[FFmpegEncoder])
      .to(classOf[FFmpegEncoderImpl])
  }

}