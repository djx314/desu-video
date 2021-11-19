package desu.service

import cats.effect.IO.Blocking
import desu.config.AppConfig
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo._

import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect.{IO => CIO, _}
import cats.implicits._
import desu.models.DirInfo

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters._
import desu.number.CollectContext

class RootFilePathsService(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val ctx = MysqlContext
  import ctx._

  object nctx extends CollectContext[CIO]
  import nctx._

  private def rootPathFileToId(name: String) = quote {
    dirMappingDao.query.filter(s => s.filePath == lift(name) && s.parentId == -1).take(1)
  }

  private def insertRootPathFileToId(name: String) = quote {
    dirMappingDao.query.insert(dirMapping(id = 0, filePath = lift(name), parentId = -1)).returningGenerated(_.id)
  }

  private def rootPathFile(dirName: String): CIO[dirMapping] = {
    def getOrSave(dir: Option[dirMapping]) = dir match {
      case Some(s) => IO(s)
      case None    => for (id <- ctx.runIO(insertRootPathFileToId(dirName))) yield dirMapping(id = id, filePath = dirName, parentId = -1)
    }

    val dirIO = for {
      findPaths <- ctx.runIO(rootPathFileToId(dirName))
      dir       <- getOrSave(findPaths.headOption)
    } yield dir

    effectIO(dirIO.transactional)
  }

  def rootPathDirInfo(dirName: String): CIO[Option[DirInfo]] = {
    // 文件不存在返回空，存在则下一步
    def getInfo(exist: Boolean, path: Path) = if (exist) {
      CIO(Option.empty)
    } else
      for {
        isDir <- CIO.blocking(Files.isDirectory(path))
        dir   <- idDirDo(isDir, path)
      } yield Option(dir)

    def listFileNames(path: Path): List[String] = {
      val files = Files.list(path).collect(Collectors.toList[Path])
      for (item <- files.asScala.to(List)) yield item.getFileName.toString
    }

    // 文件是文件夹则列出文件夹，如果是普通文件则返回文件信息
    def idDirDo(isDir: Boolean, path: Path) = if (isDir) {
      for {
        mapping <- rootPathFile(dirName)
        files   <- CIO.blocking(listFileNames(path))
      } yield DirInfo(dirInfo = mapping, files, isDir = isDir)
    } else {
      for (mapping <- rootPathFile(dirName)) yield DirInfo(dirInfo = mapping, List.empty, isDir = isDir)
    }

    def currentDir = appConfig.rootFilePath.resolve(dirName)

    val action = for {
      cDir  <- flatMap(CIO.blocking(currentDir))
      exist <- flatMap(CIO.blocking(Files.exists(cDir)))
      dir   <- map(getInfo(exist, cDir))
    } yield dir
    runF(action)
  }

}
