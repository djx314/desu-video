package desu.service

import desu.config.AppConfig

import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import scala.jdk.CollectionConverters.*
import cats.*
import cats.syntax.all.*
import cats.effect.*

import desu.models.*

import doobie.*
import doobie.implicits.given

trait FileFinder(appConfig: AppConfig, xa: Transactor[IO]):

  def rootPathFiles: IO[RootPathFiles] =
    def fileList(path: Path) = Files.list(path).map(_.toFile.getName).collect(Collectors.toList[String])

    for
      path  <- appConfig.rootPath
      model <- IO.blocking(fileList(path))
    yield
      val files = model.asScala.to(List)
      RootPathFiles(files = files)
  end rootPathFiles

end FileFinder

class FileFinderImpl(using AppConfig, Transactor[IO]) extends FileFinder(summon, summon)
