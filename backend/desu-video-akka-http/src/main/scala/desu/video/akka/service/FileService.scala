package desu.video.akka.service

import desu.video.akka.config.AppConfig
import desu.video.akka.model.{DirId, RootPathFiles}
import desu.video.common.slick.DesuDatabase

import java.nio.file.Files
import java.util.stream.Collectors
import scala.concurrent.{blocking, ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import io.circe._
import io.circe.syntax._
import desu.video.common.slick.model.Tables._
import desu.video.common.slick.model.Tables.profile.api._

class FileService(appConfig: AppConfig, desuDatabase: DesuDatabase)(implicit ec: ExecutionContext) {

  val db = desuDatabase.db

  def rootPathFiles: Future[RootPathFiles] = {
    def fileStream = Files.list(appConfig.rootPath).map(_.toFile.getName)
    Future {
      // 再次判断文件是否存在或者是否文件夹
      val confirm = blocking(Files.exists(appConfig.rootPath) && Files.isDirectory(appConfig.rootPath))
      if (confirm) {
        val l = blocking(fileStream.collect(Collectors.toList[String]))
        RootPathFiles(dirConfirm = confirm, files = l.asScala.to(List))
      } else RootPathFiles(dirConfirm = false, files = List.empty)
    }
  }

  def rootPathRequestFileId(fileName: String): Future[DirId] = {
    val fileNameJson = List(fileName).asJson.noSpaces

    val dirMappingOptF = db.run(DirMapping.filter(_.filePath === fileNameJson).result.headOption)

    def insertQuery = DirMapping returning DirMapping.map(_.id) into ((model, id) => model.copy(id = id))
    def notExistsF  = db.run(insertQuery += DirMappingRow(id = -1, filePath = fileName))

    def fromOpt(opt: Option[DirMappingRow]) = opt match {
      case Some(dirMapping) => Future.successful(dirMapping)
      case None             => notExistsF
    }

    for {
      dirOpt     <- dirMappingOptF
      dirMapping <- fromOpt(dirOpt)
    } yield DirId(id = dirMapping.id, fileName = dirMapping.filePath)
  }

}
