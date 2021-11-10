package desu.service

import desu.config.AppConfig
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo._
import scala.concurrent.ExecutionContext.Implicits.global

class FilePageRoute(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val ctx = MysqlContext
  import ctx._

  val rootPathFileToId = quote { (dirName: String) =>
    dirMappingDao.query.filter(s => s.filePath == lift(dirName) && s.parentId == -1).take(1)
  }

  val insertRootPathFileToId = quote { (name: String) =>
    dirMappingDao.query.insert(dirMapping(id = 0, filePath = lift(name), parentId = -1)).returningGenerated(_.id)
  }

  def rootPathFile(name: String): IO[dirMapping, Effect.Write with Effect.Read] = {
    def getOrSave(dir: Option[dirMapping]) = dir match {
      case Some(s) => IO(s)
      case None    => for (id <- ctx.runIO(insertRootPathFileToId(name))) yield dirMapping(id = id, filePath = name, parentId = -1)
    }

    val findPathsIO = ctx.runIO(rootPathFileToId(name))

    for {
      findPaths <- findPathsIO
      dir       <- getOrSave(findPaths.headOption)
    } yield dir
  }

}
