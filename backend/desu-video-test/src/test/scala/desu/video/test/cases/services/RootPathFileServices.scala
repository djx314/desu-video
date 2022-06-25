package desu.video.test.cases.services

import desu.video.test.cases.mainapp.DesuConfig
import zio.{EnvironmentTag, *}

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters
import io.getquill.*
import desu.video.common.quill.model.desuVideo.*
import desu.video.test.cases.mainapp.MysqlJdbcContext as ctx
import desu.video.test.model.*

import scala.util.Try
import desu.video.test.cases.model.JsonCodec.given
import com.github.plokhotnyuk.jsoniter_scala.core.*
import desu.video.test.cases.mainapp.*

import scala.jdk.CollectionConverters.given
import javax.sql.DataSource

class RootPathFileServices:

  val resloveRootFiles: DTask[List[String]] =
    val pathZIO             = ZIO.serviceWith[DesuConfig](_.desu.video.file.rootPath)
    def forPath(path: Path) = for file <- Files.list(path) yield file.toFile.getName
    for
      pathName <- pathZIO
      path     <- ZIO.succeed(Paths.get(pathName))
      nameCol  <- ZIO.attemptBlocking(forPath(path))
    yield
      val nameList = nameCol.collect(Collectors.toList[String])
      nameList.asScala.to(List)
  end resloveRootFiles

end RootPathFileServices

object RootPathFileServices:

  val layer: URLayer[DesuConfig, RootPathFileServices]           = ZLayer.succeed(new RootPathFileServices)
  val resloveRootFiles: DRIO[RootPathFileServices, List[String]] = ZIO.serviceWithZIO[RootPathFileServices](_.resloveRootFiles)

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

class ResolveFileNameService:

  import ctx.*

  private inline def fileNameQuery(dirId: Long) = quote {
    query[dirMapping].filter(_.id == lift(dirId))
  }

  def dirInfoFromId(dirId: Long): DTask[List[(DirId, FilePathParentId)]] = {
    val fileNameZio = ctx.run(fileNameQuery(dirId))

    def convertModelImpl(dirMapping: dirMapping) = for fileNameList <- DBFileNameUtil.takeFileName(dirMapping.filePath)
    yield DirId(id = dirMapping.id, fileName = fileNameList.head) -> FilePathParentId(dirMapping.parentId)

    val convertModel = convertModelImpl.andThen(_.mapError(Option.apply))

    for
      list   <- fileNameZio
      target <- ZIO.collect(list)(convertModel)
    yield target
  }.provideD(DBFileNameUtil.live)

end ResolveFileNameService

object ResolveFileNameService:

  val layer: URLayer[DataSource, ResolveFileNameService] = ZLayer.succeed(new ResolveFileNameService)

  def dirInfoFromId(dirId: Long): DRIO[ResolveFileNameService, List[(DirId, FilePathParentId)]] =
    ZIO.serviceWithZIO[ResolveFileNameService](_.dirInfoFromId(dirId))

end ResolveFileNameService
