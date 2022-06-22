package zdesu.service

import zio._
import zdesu.model.DirId
import desu.video.common.slick.model.Tables._
import profile.api._
import zdesu.mainapp._
import tethys._
import tethys.jackson._

import scala.concurrent.ExecutionContext

case class FileService(slickDBAction: SlickDBAction) {

  def rootPathRequestFileId(fileName: String): Task[DirId] = {

    val fileNameJson = List(fileName).asJson

    def dirMappingDBIO = DirMapping.filter(_.filePath === fileNameJson).take(1).to[List].result.headOption

    def modelToImport = DirMappingRow(id = -1, filePath = fileNameJson, parentId = -1)
    val notExistsZio  = DirMapping returning DirMapping.map(_.id) into ((model, id) => model.copy(id = id))

    def fromOpt(opt: Option[DirMappingRow]) = opt match {
      case Some(dirMapping) => DBIO.successful(dirMapping)
      case None             => notExistsZio += modelToImport
    }

    def action(implicit ec: ExecutionContext) = for {
      dirOpt     <- dirMappingDBIO
      dirMapping <- fromOpt(dirOpt)
    } yield DirId(
      id = dirMapping.id,
      fileName = dirMapping.filePath.jsonAs[List[String]].getOrElse(List.empty).head
    )

    zioDB.runWith(implicit e => action)

  }.provideEnvironment(ZEnvironment(slickDBAction))

}

object FileService {

  val live: URLayer[SlickDBAction, FileService]                        = ZLayer.fromFunction(FileService.apply _)
  def rootPathRequestFileId(fileName: String): RIO[FileService, DirId] = ZIO.serviceWithZIO[FileService](_.rootPathRequestFileId(fileName))

}
