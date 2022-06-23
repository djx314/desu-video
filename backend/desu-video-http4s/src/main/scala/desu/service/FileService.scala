package desu.video.akka.service

import desu.config.AppConfig

import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import scala.jdk.CollectionConverters.*
import cats.*
import cats.syntax.all.*
import cats.effect.*

import desu.models.*

import doobie.*
import doobie.implicits.given

import io.circe.syntax.*
import desu.video.common.quill.model.desuVideo.*

class FileService(using AppConfig, Transactor[IO]) {

  private val appConfig = summon[AppConfig]
  private val xa        = summon[Transactor[IO]]

  def rootPathRequestFileId(fileName: String): IO[DirId] = {
    val fileNameJson = List(fileName).asJson.noSpaces

    val dirMappingEither =
      sql"select id, file_path, parent_id from dir_mapping where file_path == ${fileNameJson} take 1".query[dirMapping].option

    dirMappingEither.transact(xa)

    ???

    /*inline def dirMappingOpt = quote {
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
      fileName = JsonDecoder[List[String]].decodeJson(dirMapping.filePath).getOrElse(List.empty).head
    )*/
  }

}
