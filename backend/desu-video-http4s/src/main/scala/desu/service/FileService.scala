package desu.service

import desu.config.AppConfig

import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import scala.jdk.CollectionConverters.*
import cats.*
import cats.syntax.all.*
import cats.effect.*
import cats.data.*

import desu.models.*

import doobie.*
import doobie.implicits.given

import io.circe.syntax.*
import desu.video.common.quill.model.desuVideo.*

trait FileService(appConfig: AppConfig, xa: Transactor[IO]):

  val y = xa.yolo
  import y.*

  def rootPathRequestFileId(fileName: String): IO[DirId] =
    val fileNameJson = List(fileName).asJson.noSpaces

    val dirMappingOptionImpl =
      sql"select id, file_path, parent_id from dir_mapping where file_path = $fileNameJson and parent_id < 0 limit 1"
        .query[dirMapping]
        .option

    val dirMappingOption: OptionT[ConnectionIO, dirMapping] = OptionT(dirMappingOptionImpl)

    val notExistsAction: ConnectionIO[dirMapping] =
      val m = dirMapping(id = -1, filePath = fileNameJson, parentId = -1)

      import m.*
      val iAction =
        sql"insert into dir_mapping (file_path, parent_id) values ($filePath, $parentId)".update.withUniqueGeneratedKeys[Int]("id")

      for id <- iAction yield m.copy(id = id)
    end notExistsAction

    val executeDBInsert = dirMappingOption.getOrElseF(notExistsAction)

    val action =
      for dirMapping <- executeDBInsert
      yield DirId(id = dirMapping.id, fileName = fileName)

    action.transact(xa)
  end rootPathRequestFileId

end FileService

class FileServiceImpl(using AppConfig, Transactor[IO]) extends FileService(summon, summon)
