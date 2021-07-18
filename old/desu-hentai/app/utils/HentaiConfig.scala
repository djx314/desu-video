package utils

import javax.inject.{Inject, Singleton}

import models.DateTimeFormat
import play.api.Configuration

trait HentaiConfig {

  val rootPath: String
  val encodeSuffix: Seq[String]
  val tempDirectoryName: String
  val encoderUrl: String
  val isEncodingrUrl: String
  val selfUrl: String
  val tempFileSuffix: String
  val encodeInfoSuffix: String = "EncodeInfo"
  val nginxPort: Int

  //def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  implicit val dateFormat = DateTimeFormat("yyyy-MM-dd HH:mm:ss.SSS")
}

@Singleton
class HentaiConfigImpl @Inject() (configuration: Configuration) extends HentaiConfig {

  override val rootPath          = configuration.get[String]("djx314.hentai.root.path")
  override val encodeSuffix      = configuration.get[Seq[String]]("djx314.hentai.encode.suffix")
  override val tempDirectoryName = configuration.get[String]("djx314.hentai.encode.temp.directory.name")

  override val encoderUrl     = configuration.get[String]("djx314.hentai.url.encoder")
  override val isEncodingrUrl = configuration.get[String]("djx314.hentai.url.isEncoding")
  override val selfUrl        = configuration.get[String]("djx314.hentai.url.self")
  override val tempFileSuffix = configuration.get[String]("djx314.hentai.encode.tempFileSuffix")
  override val nginxPort      = configuration.get[Int]("djx314.hentai.url.nginxPort")

}
