package assist.controllers

import java.io.File
import java.net.{URI, URLDecoder}
import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors

import archer.controllers.CommonController
import io.circe.Decoder
import models._
import play.api.libs.circe.Circe
import play.api.libs.ws.WSClient
import play.api.mvc.ControllerComponents
import utils.{FileUtil, HentaiConfig}
import net.scalax.asuna.sample.dto2.FutureDtoHelper
import org.slf4j.LoggerFactory
import io.circe.syntax._
import org.scalax.kirito.circe.KCirce

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.util.Try
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

class FilesList(hentaiConfig: HentaiConfig, wSClient: WSClient, fileUtil: FileUtil, controllerComponents: ControllerComponents)
    extends CommonController(controllerComponents)
    with Circe
    with FutureDtoHelper {

  val logger = LoggerFactory.getLogger(getClass)

  implicit def ec = defaultExecutionContext

  import hentaiConfig._

  def fetchIfEncoding(tempDateFile: Path) = {
    TempFileInfo
      .fromUnknowPath(tempDateFile)
      .encodeUUID
      .map { uuid =>
        wSClient
          .url(hentaiConfig.isEncodingrUrl)
          .withQueryStringParameters("uuid" -> uuid)
          .get()
          .map { wsResult =>
            if (wsResult.status == 200) {
              Try {
                java.lang.Boolean.valueOf(wsResult.body): Boolean
              }.getOrElse(false)
            } else {
              false
            }
          }
          .recover {
            case e: Exception =>
              logger.error("向后端请求是否正在转码信息错误", e)
              false
          }
      }
      .getOrElse(Future.successful(false))
  }

  def at = Action.async(circe.json[PathAndHost]) { implicit request =>
    val file1       = request.body.path
    val host        = request.body.host
    val path        = rootPath
    val parentFile  = Paths.get(path)
    val currentPath = parentFile.resolve(file1)
    val infoOpt     = fileUtil.comparePath(parentFile, currentPath)

    infoOpt
      .map { info =>
        if (Files.isDirectory(currentPath)) {
          val paths = Files.list(currentPath).collect(Collectors.toList()).asScala.toList
          val fileUrlsF =
            paths.filter(_.getFileName.toString != hentaiConfig.tempDirectoryName).map { s =>
              val l = fileUtil.comparePath(parentFile, s)

              object TempFileInfoTable {
                val fileName    = s.getFileName.toString
                val isDirectory = Files.isDirectory(s)
                val requestUrl = {
                  val i =
                    l.to(List)
                      .flatMap(_.subPath)
                      .map(_.getFileName.toString)
                      .foldLeft(("/assets?path=", true)) { (uri, str) => (uri._1 + (if (uri._2) "" else "/") + str, false) }
                      ._1
                  Uri(path = assist.controllers.routes.Assets.files.url).withFragment(i).renderString
                }
                val filePath = {
                  l.to(List).flatMap(_.subPath).map(_.getFileName.toString).foldLeft(("", true)) { (uri, str) => (uri._1 + (if (uri._2) "" else "/") + str, false) }._1
                }
                val nginxUrl = {
                  val i =
                    l.to(List)
                      .flatMap(_.subPath)
                      .map(_.getFileName.toString)
                      .foldLeft(Uri(scheme = Option(Uri.Scheme.http), authority = Option(Uri.Authority(host = Uri.RegName(host), port = Option(hentaiConfig.nginxPort))))) {
                        (uri, str) => uri / str
                      }
                  i.renderString
                }
                val tempUrl = {
                  val i =
                    l.to(List)
                      .flatMap(_.subPath)
                      .dropRight(1)
                      .map(_.getFileName.toString)
                      .foldLeft(Uri(scheme = Option(Uri.Scheme.http), authority = Option(Uri.Authority(host = Uri.RegName(host), port = Option(hentaiConfig.nginxPort))))) {
                        (uri, str) => uri / str
                      }
                  val ii = i / hentaiConfig.tempDirectoryName / (s.getFileName.toString + "." + hentaiConfig.tempFileSuffix)
                  ii.renderString
                }
                val (tempFile, temfileExists) = fileUtil.tempFileExists(s, hentaiConfig.tempDirectoryName, hentaiConfig.encodeInfoSuffix)
                val canEncode                 = fileUtil.canEncode(s.getFileName.toUri.toString, hentaiConfig.encodeSuffix)
                val tempDateFile              = tempFile.getParent.resolve(s.getFileName.toString + "." + hentaiConfig.encodeInfoSuffix)
                val isEncoding                = fetchIfEncoding(tempDateFile)
              }

              dtoWithTable[FilePath](TempFileInfoTable).model
            }
          //val periPath = currentPath.getParent.toRealPath().toUri.toString
          val preiRealPath = {
            val i =
              info.subPath
                .dropRight(1)
                .map(_.getFileName.toString)
                .foldLeft(("/assets?path=", true)) { (uri, str) => (uri._1 + (if (uri._2) "" else "/") + str, false) }
                ._1

            Uri(path = assist.controllers.routes.Assets.files.url).withFragment(i).renderString
          }

          object TempDirInfo {
            val parentPath = preiRealPath
            val urls       = Future.sequence(fileUrlsF)
          }

          val dirModelf = dtoWithTable[DirInfo](TempDirInfo).model

          dirModelf.map { dirModel => Ok(dirModel.asJson) }
        } else {
          Future successful BadRequest("参数错误，请求的路径不是目录")
        }
      }
      .getOrElse {
        Future successful NotFound("找不到目录")
      }

  /*if (!Files.exists(currentPath)) {
        Future successful NotFound("找不到目录")
      } else if (Files.isDirectory(currentPath)) {
        val paths = Files.list(currentPath).collect(Collectors.toList()).asScala.toList
        val fileUrlsF =
          paths.filter(_.getFileName.toString != hentaiConfig.tempDirectoryName).map { s =>
            object TempFileInfoTable {
              val fileName                  = s.getFileName.toString
              val isDirectory               = Files.isDirectory(s)
              val requestUrl                = s.toRealPath().toUri.toASCIIString.drop(parentUrl.size)
              val (tempFile, temfileExists) = fileUtil.tempFileExists(s, hentaiConfig.tempDirectoryName, hentaiConfig.encodeInfoSuffix)
              val canEncode                 = fileUtil.canEncode(s.getFileName.toUri.toString, hentaiConfig.encodeSuffix)
              val tempDateFile              = tempFile.getParent.resolve(s.getFileName.toString + "." + hentaiConfig.encodeInfoSuffix)
              val isEncoding                = fetchIfEncoding(tempDateFile)
            }

            dtoWithTable[FilePath](TempFileInfoTable).model
          }
        val periPath = currentPath.getParent.toRealPath().toUri.toString
        val preiRealPath = if (periPath.startsWith(parentUrl)) {
          //val result = periPath.drop(parentUrl.size)
          periPath.drop(parentUrl.size)
        } else {
          //assist.controllers.routes.Assets.root
          ""
        }

        object TempDirInfo {
          val parentPath = preiRealPath.toString
          val urls       = Future.sequence(fileUrlsF)
        }

        val dirModelf = dtoWithTable[DirInfo](TempDirInfo).model

        dirModelf.map { dirModel => Ok(dirModel.asJson) }
      } else {
        Future successful BadRequest("参数错误，请求的路径不是目录")
      }*/
  }

  def atAss = Action.async(circe.json[PathInfo]) { implicit request =>
    val file       = request.body.path
    val file1      = URLDecoder.decode(file, "utf-8")
    val path       = rootPath
    val parentFile = new File(path)
    val parentUrl  = parentFile.toURI.toString
    val fileModel  = new File(parentFile, file1)
    if (!fileModel.exists) {
      Future successful NotFound("找不到目录")
    } else if (fileModel.isDirectory) {
      val fileUrls = fileModel.listFiles().toList.filter(_.getName != hentaiConfig.tempDirectoryName).map { s =>
        //val fileUrlString = s.toURI.toString.drop(parentUrl.size)
        FileSimpleInfo(fileName = s.getName, encodeUrl = s.toURI.toString.drop(parentUrl.size), isDir = s.isDirectory)
      }
      val periPath = fileModel.getParentFile.toURI.toString
      val preiRealPath = if (periPath.startsWith(parentFile.toURI.toString) && periPath != parentUrl) {
        val result = periPath.drop(parentUrl.size)
        result
      } else {
        ""
      }

      Future successful Ok(DirSimpleInfo(preiRealPath, fileUrls).asJson)
    } else {
      Future successful BadRequest("参数错误，请求的路径不是目录")
    }
  }

}

case class FileSimpleInfo(fileName: String, encodeUrl: String, isDir: Boolean)
object FileSimpleInfo {
  object emptyTable
  implicit def filePathImplicitEncoder: io.circe.Encoder[FileSimpleInfo] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[FileSimpleInfo]          = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class DirSimpleInfo(parentPath: String, urls: List[FileSimpleInfo])
object DirSimpleInfo {
  object emptyTable
  implicit def filePathImplicitEncoder: io.circe.Encoder[DirSimpleInfo] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[DirSimpleInfo]          = KCirce.decodeCaseClassWithTable(emptyTable)
}
