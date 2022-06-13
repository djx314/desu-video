package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import desu.video.akka.config.AppConfig
import desu.video.akka.model.DirId

import scala.concurrent.{ExecutionContext, Future}
import io.circe.syntax.*
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo.dirMapping
import io.getquill.*
import io.getquill.context.qzio.ImplicitSyntax.*
import zio._

class FileService(appConfig: AppConfig, mysqlContext: MysqlContext)(using system: ActorSystem[Nothing]) {

  import mysqlContext.{*, given}

  given ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("desu-dispatcher"))

  /** 未调整
    *
    * @param fileName
    * @return
    */
  def rootPathRequestFileId(fileName: String): Future[DirId] = {
    val fileNameJson = List(fileName).asJson.noSpaces

    inline def dirMappingOpt = quote {
      query[dirMapping].filter(_.filePath == lift(fileNameJson)).take(1)
    }
    val dirMappingOptZio = run(dirMappingOpt).map(_.headOption).provideLayer(dataSourceLayer)
    def dirMappingOptF   = Runtime.default.unsafeRunToFuture(dirMappingOptZio)

    def modelToImport = dirMapping(id = -1, filePath = fileNameJson, parentId = -1)
    inline def insertQuery = quote {
      query[dirMapping].insertValue(lift(modelToImport)).returning(_.id)
    }
    val notExistsZio = run(insertQuery).map(id => modelToImport.copy(id = id)).provideLayer(dataSourceLayer)

    def fromOpt(opt: Option[dirMapping]) = opt match {
      case Some(dirMapping) => Future.successful(dirMapping)
      case None             => Runtime.default.unsafeRunToFuture(notExistsZio)
    }

    for {
      dirOpt     <- dirMappingOptF
      dirMapping <- fromOpt(dirOpt)
    } yield DirId(
      id = dirMapping.id,
      fileName = io.circe.parser.decode[List[String]](dirMapping.filePath).getOrElse(List.empty).head
    )
  }

}
