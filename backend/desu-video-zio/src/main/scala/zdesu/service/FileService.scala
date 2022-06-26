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

  private implicit class DBIOMethod[E](private val dbio: DBIO[Option[E]]) {
    def orElse(default: => DBIO[E])(implicit ec: ExecutionContext): DBIO[E] = dbio.flatMap {
      case Some(s) => DBIO.successful(s)
      case None    => default
    }
  }

  def rootPathRequestFileId(fileName: String): Task[DirId] = {

    val fileNameJson = List(fileName).asJson

    def dirMappingDBIO = DirMapping.filter(s => s.filePath === fileNameJson && s.parentId < 0).take(1).to[List].result.headOption

    def modelToImport         = DirMappingRow(id = -1, filePath = fileNameJson, parentId = -1)
    val notExistsDBIO         = DirMapping returning DirMapping.map(_.id) into ((model, id) => model.copy(id = id))
    def notExistsInsertAction = notExistsDBIO += modelToImport

    val dirMappingF = zioDB.runWith(implicit ec => dirMappingDBIO.orElse(notExistsInsertAction))

    for (dirMapping <- dirMappingF) yield DirId(id = dirMapping.id, fileName = fileName)

  }.provideEnvironment(ZEnvironment(slickDBAction))

}

object FileService {

  val live: URLayer[SlickDBAction, FileService]                        = ZLayer.fromFunction(FileService.apply _)
  def rootPathRequestFileId(fileName: String): RIO[FileService, DirId] = ZIO.serviceWithZIO[FileService](_.rootPathRequestFileId(fileName))

}
