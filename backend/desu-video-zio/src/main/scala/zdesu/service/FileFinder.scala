package zdesu.service

import zdesu.mainapp.DesuConfig
import zdesu.model.RootPathFiles
import zio._

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters._

case class FileFinder(desuConfig: DesuConfig) {

  def rootPathFiles: Task[RootPathFiles] = {
    def fileList = Files.list(Paths.get(desuConfig.desu.video.file.rootPath)).map(_.toFile.getName).collect(Collectors.toList[String])

    for (model <- ZIO.attemptBlocking(fileList)) yield {
      val files = model.asScala.to(List)
      RootPathFiles(files = files)
    }
  }

}

object FileFinder {

  val live: URLayer[DesuConfig, FileFinder] = ZLayer.fromFunction(FileFinder.apply _)

  val rootPathFiles: RIO[FileFinder, RootPathFiles] = ZIO.serviceWithZIO[FileFinder](_.rootPathFiles)

}
