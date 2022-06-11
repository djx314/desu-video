package desu.video.test.cases.services

import desu.video.test.cases.mainapp.DesuConfig
import zio.*

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters.given
import io.getquill.*
import desu.video.common.quill.model.desuVideo.*
import desu.video.test.cases.mainapp.{MysqlJdbcContext => ctx}
import desu.video.test.model.*

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

case class ResolveFileNameService(dataSource: DataSource):

  import ctx.*

  def dirInfoFromId(dirId: Long): Task[List[DirId]] =
    inline def fileNameQuery = quote {
      query[dirMapping].filter(_.id == lift(dirId))
    }
    val fileNameZio = ctx.run(fileNameQuery)

    def modelToDirId(dirMapping: dirMapping) =
      def decodeResult = io.circe.parser.decode[List[String]](dirMapping.filePath)
      for
        a1   <- ZIO.fromEither(decodeResult)
        name <- ZIO.attempt(a1.head)
      yield DirId(id = dirMapping.id, fileName = name)
    end modelToDirId

    val action = for
      dirMappings <- fileNameZio
      list = dirMappings.map(modelToDirId)
      coll <- ZIO.collectAll(list)
    yield coll

    action.provideEnvironment(ZEnvironment(dataSource))
  end dirInfoFromId

end ResolveFileNameService

object ResolveFileNameService:

  val layer: URLayer[DataSource, ResolveFileNameService] = ZLayer.fromFunction(ResolveFileNameService.apply)

  def dirInfoFromId(dirId: Long): RIO[ResolveFileNameService, List[DirId]] =
    ZIO.serviceWithZIO[ResolveFileNameService](_.dirInfoFromId(dirId))

end ResolveFileNameService
