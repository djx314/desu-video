package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import desu.video.akka.config.AppConfig
import desu.video.akka.model.DirId
import desu.video.common.slick.DesuDatabase

import scala.concurrent.Future
import io.circe.syntax._
import desu.video.common.slick.model.Tables._
import desu.video.common.slick.model.Tables.profile.api._

class FileService(appConfig: AppConfig, desuDatabase: DesuDatabase)(implicit system: ActorSystem[Nothing]) {
  implicit val executionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("desu-dispatcher"))

  val db = desuDatabase.db

  /** 未调整
    * @param fileName
    * @return
    */
  def rootPathRequestFileId(fileName: String): Future[DirId] = {
    val fileNameJson = List(fileName).asJson.noSpaces

    val dirMappingOptF = db.run(DirMapping.filter(_.filePath === fileNameJson).result.headOption)

    def insertQuery = DirMapping returning DirMapping.map(_.id) into ((model, id) => model.copy(id = id))
    def notExistsF  = db.run(insertQuery += DirMappingRow(id = -1, filePath = fileNameJson, parentId = -1))

    def fromOpt(opt: Option[DirMappingRow]) = opt match {
      case Some(dirMapping) => Future.successful(dirMapping)
      case None             => notExistsF
    }

    for {
      dirOpt     <- dirMappingOptF
      dirMapping <- fromOpt(dirOpt)
    } yield DirId(
      id = dirMapping.id,
      fileName = io.circe.parser.parse(dirMapping.filePath).flatMap(_.asJson.as[List[String]]).getOrElse(List.empty).head
    )
  }

}
