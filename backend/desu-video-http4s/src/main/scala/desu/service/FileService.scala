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

    val dirMappingOption: ConnectionIO[Option[dirMapping]] =
      sql"select id, file_path, parent_id from dir_mapping where file_path = $fileNameJson limit 1".query[dirMapping].option

    def modelToImport = dirMapping(id = -1, filePath = fileNameJson, parentId = -1)

    def insertAction(m: dirMapping): ConnectionIO[Int] =
      import m.*
      sql"insert into dir_mapping (file_path, parent_id) values ($filePath, $parentId)".update.withUniqueGeneratedKeys[Int]("id")
    end insertAction

    def notExistsAction(m: dirMapping): ConnectionIO[dirMapping] = for id <- insertAction(m) yield m.copy(id = id)

    def fromOpt(opt: OptionT[ConnectionIO, dirMapping]) = opt.foldF(notExistsAction(modelToImport))(Applicative[ConnectionIO].pure)

    val action =
      for dirMapping <- fromOpt(OptionT(dirMappingOption))
      yield DirId(id = dirMapping.id, fileName = io.circe.parser.decode[List[String]](dirMapping.filePath).getOrElse(List.empty).head)

    action.transact(xa)
  end rootPathRequestFileId

end FileService

class FileServiceImpl(using AppConfig, Transactor[IO]) extends FileService(summon, summon)
