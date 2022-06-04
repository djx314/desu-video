package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import desu.video.akka.config.AppConfig
import desu.video.akka.model.DirId

import scala.concurrent.{ExecutionContext, Future}
import io.circe.syntax.*
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo.dirMapping
import io.getquill.*

class FileService(appConfig: AppConfig, mysqlContext: MysqlContext)(using system: ActorSystem[Nothing]) {

  import mysqlContext._

  given ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("desu-dispatcher"))

  /** 未调整
    *
    * @param fileName
    * @return
    */
  def rootPathRequestFileId(fileName: String): Future[DirId] = {
    val fileNameJson = List(fileName).asJson.noSpaces

    inline def dirMappingOptF = quote {
      query[dirMapping].filter(_.filePath == lift(fileNameJson)).take(1)
    }
    run(dirMappingOptF)

    /*def insertQuery = DirMapping returning DirMapping.map(_.id) into ((model, id) => model.copy(id = id))
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
    )*/
    ???
  }

}
