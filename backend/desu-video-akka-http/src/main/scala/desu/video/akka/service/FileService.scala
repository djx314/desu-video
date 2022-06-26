package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import desu.video.akka.config.AppConfig
import desu.video.akka.model.DirId

import scala.concurrent.{ExecutionContext, Future}
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo.dirMapping
import io.getquill.*
import io.getquill.context.qzio.ImplicitSyntax.*
import zio.*
import zio.json.*

class FileService(appConfig: AppConfig, mysqlContext: MysqlContext)(using system: ActorSystem[Nothing]) {

  import mysqlContext.{*, given}

  given ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("desu-dispatcher"))

  /** 未调整
    *
    * @param fileName
    * @return
    */
  def rootPathRequestFileId(fileName: String): Future[DirId] = {
    val fileNameJson = List(fileName).toJson

    inline def dirMappingOpt = quote {
      query[dirMapping].filter(s => s.filePath == lift(fileNameJson) && s.parentId < 0).take(1)
    }
    val dirMappingOptZio = for (s <- run(dirMappingOpt)) yield s.headOption

    def modelToImport = dirMapping(id = -1, filePath = fileNameJson, parentId = -1)
    inline def insertQuery = quote {
      query[dirMapping].insertValue(lift(modelToImport)).returning(_.id)
    }
    val notExistsZio = for (id <- run(insertQuery)) yield modelToImport.copy(id = id)

    val dirMappingOptAction = dirMappingOptZio.someOrElseZIO(notExistsZio)
    val dirMappingOptZIO    = transaction(dirMappingOptAction).provideLayer(dataSourceLayer)
    val dirMappingOptF      = Runtime.default.unsafeRunToFuture(dirMappingOptZIO)

    for (dirMapping <- dirMappingOptF)
      yield DirId(
        id = dirMapping.id,
        fileName = fileName
      )
  }

}
