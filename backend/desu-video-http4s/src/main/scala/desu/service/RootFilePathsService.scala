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
      case None    => for (id <- runIO(insertRootPathFileToId(dirName))) yield dirMapping(id = id, filePath = dirName, parentId = -1)
    }

    val dirIO = for {
      findPaths <- runIO(rootPathFileToId(dirName))
      dir       <- getOrSave(findPaths.headOption)
    } yield dir

    effectIO(dirIO.transactional)
  }

  def rootPathDirInfo(dirName: String): CIO[Option[DirInfo]] = {
    // 文件不存在返回空，存在则下一步
    def getInfo(exist: Boolean, path: Path) = if (exist) {
      for {
        isDir <- flatMap(CIO.blocking(Files.isDirectory(path)))
        dir   <- plusM(idDirDo(isDir, path))
      } yield Option(dir)
    } else liftToN(CIO(Option.empty))

    def listFileNames(path: Path): List[String] = {
      val files = Files.list(path).collect(Collectors.toList[Path])
      for (item <- files.asScala.to(List)) yield item.getFileName.toString
    }

    // 文件是文件夹则列出文件夹，如果是普通文件则返回文件信息
    def idDirDo(isDir: Boolean, path: Path) = if (isDir) {
      for {
        mapping <- flatMap(rootPathFile(dirName))
        files   <- map(CIO.blocking(listFileNames(path)))
      } yield DirInfo(dirInfo = mapping, files, isDir = isDir)
    } else {
      for (mapping <- map(rootPathFile(dirName))) yield DirInfo(dirInfo = mapping, List.empty, isDir = isDir)
    }

    def currentDir = appConfig.rootFilePath.resolve(dirName)

    val action = for {
      cDir  <- flatMap(CIO.blocking(currentDir))
      exist <- flatMap(CIO.blocking(Files.exists(cDir)))
      dir   <- plusM(getInfo(exist, cDir))
    } yield dir
    runF(action)
  }

  def rootPathDirName: CIO[Option[DirInfo]] = {
    val currentDir = appConfig.rootFilePath

    def listFileNames: List[String] = {
      val files = Files.list(currentDir).collect(Collectors.toList[Path])
      for (item <- files.asScala.to(List)) yield item.getFileName.toString
    }

    def getInfo(exist: Boolean, isDir: Boolean) = if (exist && isDir) {
      for (dir <- map(CIO.blocking(listFileNames)))
        yield Option(DirInfo(dirInfo = dirMapping(id = -1, filePath = "", parentId = -1), dir, isDir = isDir))
    } else liftToN(CIO(Option.empty))

    val action = for {
      cDir  <- flatMap(CIO.blocking(currentDir))
      exist <- flatMap(CIO.blocking(Files.exists(cDir)))
      isDir <- flatMap(CIO.blocking(Files.isDirectory(cDir)))
      info  <- plusM(getInfo(exist, isDir))
    } yield info
    runF(action)
  }

}
