package desu.video.akka.routes

import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

class FileService(implicit ec: ExecutionContext) {

  val dirPath = Paths.get(ConfigFactory.load().getString("desu.video.file.rootPath"))

  def list: Future[List[String]] = {
    def fileStream = Files.list(dirPath).map(_.toFile.getName)
    Future { fileStream.collect(Collectors.toList[String]).asScala.to(List) }
  }

}
