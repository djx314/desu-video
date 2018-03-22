package net.scalax.mp4.play

import java.io.File
import java.net.URL
import javax.inject.{ Inject, Singleton }

import controllers.{ AssetsBuilder, AssetsConfiguration, DefaultAssetsMetadata }
import play.api.http.{ FileMimeTypes, HttpErrorHandler }

@Singleton
class CustomAssets @Inject() (errorHandler: HttpErrorHandler, meta: CustomAssetsMetadata) extends AssetsBuilder(errorHandler, meta)

object FileUrlGen extends (String => Option[URL]) {
  override def apply(v1: String) = {
    val file = new File(v1)
    if (file.exists()) {
      Option(file.toURI.toURL)
    } else {
      None
    }
  }
}

@Singleton
class CustomAssetsMetadata @Inject() (
  config: AssetsConfiguration,
  fileMimeTypes: FileMimeTypes) extends DefaultAssetsMetadata(config, FileUrlGen, fileMimeTypes)