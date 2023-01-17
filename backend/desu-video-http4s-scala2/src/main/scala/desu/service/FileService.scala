package desu.service

import desu.config.AppConfig

import scala.jdk.CollectionConverters._
import cats._
import cats.effect._
import cats.data._

import desu.models._

import doobie._
import doobie.implicits._

import io.circe.syntax._
import desu.video.common.quill.model.desuVideo._

class FileService(appConfig: AppConfig, xa: Transactor[IO]) {

  val y = xa.yolo
  import y._

  def findDirMapping(encodeDirName: String): ConnectionIO[Option[dirMapping]] = {
    sql"select id, file_path, parent_id from dir_mapping where file_path = $encodeDirName and parent_id < 0 limit 1"
      .query[dirMapping]
      .option
  }

  def insertNewDirMapping(dirMapping: dirMapping): ConnectionIO[dirMapping] = {
    val iAction = sql"insert into dir_mapping (file_path, parent_id) values (${dirMapping.filePath}, ${dirMapping.parentId})".update
      .withUniqueGeneratedKeys[Int]("id")
    for (id <- iAction) yield dirMapping.copy(id = id)
  }

  def rootPathRequestFileId(fileName: String): IO[DirId] = {
    val encodeFileName = List(fileName).asJson.noSpaces
    def preModel       = dirMapping(id = -1, filePath = encodeFileName, parentId = -1)

    val dirMappingOption: OptionT[ConnectionIO, dirMapping] = OptionT(findDirMapping(encodeFileName))
    def noExistAction                                       = insertNewDirMapping(preModel)
    val executeDBInsert: ConnectionIO[dirMapping]           = dirMappingOption.getOrElseF(noExistAction)

    val action = executeDBInsert.transact(xa)
    for (dirMapping <- action) yield DirId(id = dirMapping.id, fileName = fileName)
  }

}

object FileService {
  def build(implicit appConfig: AppConfig, tx: Transactor[IO]): FileService = new FileService(implicitly, implicitly)
}
