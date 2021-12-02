package desu.service

import desu.config.AppConfig
import desu.video.common.quill.model.MysqlContext
import desu.video.common.quill.model.desuVideo._

import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect._
import cats.implicits._
import desu.models.{DirInfo, FileItem}

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters._

class RootFilePathsService(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val ctx = MysqlContext
  import ctx.{IO => QIO, _}

  private def rootPathFileToId(name: String) = quote {
    dirMappingDao.query.filter(s => s.filePath == lift(name) && s.parentId == -1).take(1)
  }

  private def insertRootPathFileToId(name: String) = quote {
    dirMappingDao.query.insert(dirMapping(id = 0, filePath = lift(name), parentId = -1)).returningGenerated(_.id)
  }

  private def rootPathFile(dirName: String): IO[dirMapping] = {
    def getOrSave(dir: Option[dirMapping]) = dir match {
      case Some(s) => QIO(s)
      case None    => for (id <- runIO(insertRootPathFileToId(dirName))) yield dirMapping(id = id, filePath = dirName, parentId = -1)
    }

    val dirIO = for {
      findPaths <- runIO(rootPathFileToId(dirName))
      dir       <- getOrSave(findPaths.headOption)
    } yield dir

    effectIO(dirIO.transactional)
  }

  private def listFileNames(dir: Path): List[FileItem] = {
    val files = Files.list(dir).collect(Collectors.toList[Path])
    for (item <- files.asScala.to(List)) yield {
      val fileName = item.getFileName.toString
      val isDir    = Files.isDirectory(item)
      FileItem(fileName = fileName, isDir = isDir)
    }
  }

  def rootPathDirInfo(dirName: String): IO[Option[DirInfo]] = {
    // 文件不存在返回空，存在则下一步
    def getInfo(exist: Boolean, path: Path) = if (exist) {
      for {
        isDir <- IO.blocking(Files.isDirectory(path))
        dir   <- idDirDo(isDir, path)
      } yield Option(dir)
    } else IO(Option.empty)

    // 文件是文件夹则列出文件夹，如果是普通文件则返回文件信息
    def idDirDo(isDir: Boolean, path: Path) = {
      if (isDir)
        for {
          mapping <- rootPathFile(dirName)
          files   <- IO.blocking(listFileNames(path))
        } yield DirInfo(dirInfo = mapping, files, isDir = isDir)
      else
        for (mapping <- rootPathFile(dirName)) yield DirInfo(dirInfo = mapping, List.empty, isDir = isDir)
    }

    def currentDir = appConfig.rootFilePath.resolve(dirName)

    for {
      cDir  <- IO.blocking(currentDir)
      exist <- IO.blocking(Files.exists(cDir))
      dir   <- getInfo(exist, cDir)
    } yield dir
  }

  def rootPathDirName: IO[Option[DirInfo]] = {
    val currentDir = appConfig.rootFilePath

    def getInfo(exist: Boolean, isDir: Boolean) = {
      if (exist && isDir)
        for (dir <- IO.blocking(listFileNames(currentDir)))
          yield Option(DirInfo(dirInfo = dirMapping(id = -1, filePath = "", parentId = -1), dir, isDir = isDir))
      else
        IO(Option.empty)
    }

    for {
      cDir  <- IO.blocking(currentDir)
      exist <- IO.blocking(Files.exists(cDir))
      isDir <- IO.blocking(Files.isDirectory(cDir))
      info  <- getInfo(exist, isDir)
    } yield info
  }

}
