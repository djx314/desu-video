package desu.service

import desu.config.AppConfig

import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import scala.jdk.CollectionConverters._
import cats._
import cats.syntax.all._
import cats.effect._

import desu.models._

import doobie._
import doobie.implicits._

class FileFinder(appConfig: AppConfig, xa: Transactor[IO]) {

  def rootPathFiles: IO[RootPathFiles] = {
    def fileList(path: Path) = Files.list(path).map(_.toFile.getName).collect(Collectors.toList[String])

    for (model <- IO.blocking(fileList(appConfig.rootPath))) yield {
      val files = model.asScala.to(List)
      RootPathFiles(files = files)
    }
  }

}

object FileFinder {
  def build(implicit appConfig: AppConfig, tx: Transactor[IO]): FileFinder = new FileFinder(implicitly, implicitly)
}
