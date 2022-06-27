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
import language.experimental.fewerBraces
import cats.effect.cps.*

trait FileService(appConfig: AppConfig, xa: Transactor[IO]):

  val y = xa.yolo
  import y.{*, given}

  def findDirMapping(encodeDirName: String): ConnectionIO[Option[dirMapping]] =
    sql"select id, file_path, parent_id from dir_mapping where file_path = $encodeDirName and parent_id < 0 limit 1"
      .query[dirMapping]
      .option

  def insertNewDirMapping(dirMapping: dirMapping): ConnectionIO[dirMapping] =
    val iAction = sql"insert into dir_mapping (file_path, parent_id) values (${dirMapping.filePath}, ${dirMapping.parentId})".update
      .withUniqueGeneratedKeys[Int]("id")
    for id <- iAction yield dirMapping.copy(id = id)
  end insertNewDirMapping

  def rootPathRequestFileId(fileName: String): IO[DirId] =
    val encodeFileName = List(fileName).asJson.noSpaces
    def preModel       = dirMapping(id = -1, filePath = encodeFileName, parentId = -1)

    val dirMappingOption: OptionT[ConnectionIO, dirMapping] = OptionT(findDirMapping(encodeFileName))
    def noExistAction                                       = insertNewDirMapping(preModel)
    val executeDBInsert: ConnectionIO[dirMapping]           = dirMappingOption.getOrElseF(noExistAction)

    val action = executeDBInsert.transact(xa)
    for dirMapping <- action yield DirId(id = dirMapping.id, fileName = fileName)
  end rootPathRequestFileId

end FileService

class FileServiceImpl(using AppConfig, Transactor[IO]) extends FileService(summon, summon)
