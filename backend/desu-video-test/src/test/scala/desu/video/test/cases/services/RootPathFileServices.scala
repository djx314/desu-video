package desu.video.test.cases.services

import desu.video.test.cases.mainapp.DesuConfig
import zio.*

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters.given
import io.getquill.*
import desu.video.common.quill.model.desuVideo.*
import desu.video.test.cases.mainapp.MysqlJdbcContext as ctx
import desu.video.test.model.*
import scala.util.Try

import desu.video.test.cases.model.JsonCodec.given
import com.github.plokhotnyuk.jsoniter_scala.core.*

import javax.sql.DataSource

case class RootPathFileServices(config: DesuConfig):

  val resloveRootFiles: Task[List[String]] =
    val path      = config.desu.video.file.rootPath
    def forPath   = for file <- Files.list(Paths.get(path)) yield file.toFile.getName
    def pathNames = forPath.collect(Collectors.toList[String])
    for names <- ZIO.attempt(pathNames) yield names.asScala.to(List)
  end resloveRootFiles

end RootPathFileServices

object RootPathFileServices:

  val layer: URLayer[DesuConfig, RootPathFileServices]          = ZLayer.fromFunction(RootPathFileServices.apply)
  val resloveRootFiles: RIO[RootPathFileServices, List[String]] = ZIO.serviceWithZIO[RootPathFileServices](_.resloveRootFiles)

end RootPathFileServices

opaque type FilePathParentId = Int

object FilePathParentId:
  inline def apply(inline i: Int): FilePathParentId                   = i
  extension (inline p: FilePathParentId) inline def isParent: Boolean = p < 0
end FilePathParentId

class DBFileNameUtil {
  def takeFileName(name: String): Task[NonEmptyChunk[String]] = for
    fileNameList  <- ZIO.attempt(readFromString[List[String]](name))
    fileNameList1 <- ZIO.attempt(NonEmptyChunk.fromIterable(fileNameList.head, fileNameList.tail))
  yield fileNameList1
  end takeFileName
}

object DBFileNameUtil {
  val live                                                                   = ZLayer.succeed(new DBFileNameUtil)
  def takeFileName(name: String): RIO[DBFileNameUtil, NonEmptyChunk[String]] = ZIO.serviceWithZIO[DBFileNameUtil](_.takeFileName(name))
}

case class ResolveFileNameService(dataSource: DataSource):

  import ctx.*

  private inline def fileNameQuery(dirId: Long) = quote {
    query[dirMapping].filter(_.id == lift(dirId))
  }

  def dirInfoFromId(dirId: Long): Task[List[(DirId, FilePathParentId)]] =
    val fileNameZio = ctx.run(fileNameQuery(dirId))

    def convertModelImpl(dirMapping: dirMapping) =
      for
        fileNameList <- DBFileNameUtil.takeFileName(dirMapping.filePath)
        fileName     <- ZIO.attempt(fileNameList.head)
      yield DirId(id = dirMapping.id, fileName = fileName) -> FilePathParentId(dirMapping.parentId)

    val convertModel = convertModelImpl.andThen(_.mapError(Option.apply))

    val action = for
      list   <- fileNameZio
      target <- ZIO.collect(list)(convertModel)
    yield target

    action.provide(DBFileNameUtil.live ++ ZLayer.succeed(dataSource))
  end dirInfoFromId

end ResolveFileNameService

object ResolveFileNameService:

  val layer: URLayer[DataSource, ResolveFileNameService] = ZLayer.fromFunction(ResolveFileNameService.apply)

  def dirInfoFromId(dirId: Long): RIO[ResolveFileNameService, List[(DirId, FilePathParentId)]] =
    ZIO.serviceWithZIO[ResolveFileNameService](_.dirInfoFromId(dirId))

end ResolveFileNameService
