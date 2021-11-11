package desu.service

import desu.config.AppConfig
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo._
import scala.concurrent.ExecutionContext.Implicits.global

class RootFilePathsService(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val ctx = MysqlContext
  import ctx._

  def rootPathFileToId(name: String) = quote {
    dirMappingDao.query.filter(s => s.filePath == lift(name) && s.parentId == -1).take(1)
  }

  def insertRootPathFileToId(name: String) = quote {
    dirMappingDao.query.insert(dirMapping(id = 0, filePath = lift(name), parentId = -1)).returningGenerated(_.id)
  }

  def rootPathFile(dirName: String): IO[dirMapping, Effect.Write with Effect.Read] = {
    def getOrSave(dir: Option[dirMapping]) = dir match {
      case Some(s) => IO(s)
      case None    => for (id <- ctx.runIO(insertRootPathFileToId(dirName))) yield dirMapping(id = id, filePath = dirName, parentId = -1)
    }

    val findPathsIO = ctx.runIO(rootPathFileToId(dirName))

    for {
      findPaths <- findPathsIO
      dir       <- getOrSave(findPaths.headOption)
    } yield dir
  }

}
