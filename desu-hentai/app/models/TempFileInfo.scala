package models

import java.nio.file.{Files, Path}

import org.joda.time.DateTime
import io.circe._
import io.circe.syntax._

import scala.jdk.CollectionConverters._

case class DateTimeFormat(format: String)

case class TempFileInfo(
  encodeUUID: Option[String] = None,
  encodeTime: Option[DateTime] = None,
  encodeSuffix: String = "mp4",
  assFilePath: Option[String] = None,
  assScale: BigDecimal = 1
) {
  self =>

  private def indented(indent: String): Printer =
    Printer(
      sortKeys = true,
      dropNullValues = false,
      indent = indent,
      lbraceRight = "\r\n",
      rbraceLeft = "\r\n",
      lbracketRight = "\r\n",
      rbracketLeft = "\r\n",
      lrbracketsEmpty = "\r\n",
      arrayCommaRight = "\r\n",
      objectCommaRight = "\r\n",
      colonLeft = " ",
      colonRight = " "
    )

  val printer: Printer = indented("  ")

  def beautifulJson(implicit df: DateTimeFormat): String = self.asJson.printWith(printer)

}

object TempFileInfo {

  def empty = TempFileInfo()

  import io.circe.generic.extras.auto._
  import io.circe.generic.extras.Configuration
  import org.joda.time.format.{DateTimeFormat => JodaDateTimeFormat}

  private implicit val configure: Configuration = Configuration.default.withDefaults

  implicit def decoder(implicit formatter: DateTimeFormat): Decoder[TempFileInfo] = {
    val format = JodaDateTimeFormat.forPattern(formatter.format)
    implicit val timeDecoder: Decoder[DateTime] = Decoder.decodeString.emap { str =>
      try {
        Right(DateTime.parse(str, format))
      } catch {
        case e: IllegalArgumentException =>
          Left(e.getLocalizedMessage)
      }
    }
    exportDecoder[TempFileInfo].instance
  }

  implicit def encoder(implicit formatter: DateTimeFormat): Encoder[TempFileInfo] = {
    implicit val timeEncoder: Encoder[DateTime] = Encoder.encodeString.contramap[DateTime](_.toString(formatter.format))
    exportEncoder[TempFileInfo].instance
  }

  def fromUnknowString(str: String)(implicit df: DateTimeFormat): TempFileInfo = {
    val json = io.circe.parser.parse(str) match {
      case Left(e) => Json.obj()
      case Right(j) =>
        if (j.isObject && !j.isNull) {
          j
        } else {
          Json.obj()
        }
    }
    json.as[TempFileInfo] match {
      case Left(e)      => empty
      case Right(model) => model
    }
  }

  def fromUnknowPath(path: Path)(implicit df: DateTimeFormat): TempFileInfo = {
    if (Files.exists(path)) {
      val str = Files.readAllLines(path).asScala.mkString
      val json = io.circe.parser.parse(str) match {
        case Left(e) => Json.obj()
        case Right(j) =>
          if (j.isObject && !j.isNull) {
            j
          } else {
            Json.obj()
          }
      }
      json.as[TempFileInfo] match {
        case Left(e)      => empty
        case Right(model) => model
      }
    } else {
      empty
    }
  }

}
