package desu.video.akka.routes

import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors
import java.util.stream.Collectors

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

object FileList {

  implicit val ec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  val dirPath = Paths.get(ConfigFactory.load().getString("desu.video.file.rootPath"))

  def list: Future[List[String]] = {
    def fileStream = Files.list(dirPath).map(_.toFile.getName)
    Future { fileStream.collect(Collectors.toList[String]).asScala.to(List) }
  }

}
