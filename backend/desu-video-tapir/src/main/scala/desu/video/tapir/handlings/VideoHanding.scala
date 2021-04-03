package desu.video.tapir.handlings

import desu.video.tapir.config.AppConfig
import desu.video.tapir.endpoints.VideoEndpoints
import desu.video.tapir.mainapp.Layers
import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import scala.jdk.CollectionConverters._
import sttp.tapir.ztapir._
import sttp.tapir.generic.auto._

import zio.ZIO

object VideoHanding {

  val fileList = VideoEndpoints.fileList.zServerLogic { case (pathId, pathModel) =>
    def javaCollectFiles(path: Path) = Files.list(path).collect(Collectors.toList[Path])
    val i = for {
      path <- AppConfig.rootPath
      b    <- ZIO.effect(javaCollectFiles(path).asScala.to(List))
    } yield b.map(_.toFile.getName)
    i.mapError((_: Throwable) => "接口出现问题。")
  }

  val routes: List[ZServerEndpoint[Layers.AppContext, _, _, _]] = List(fileList.widen[Layers.AppContext])

}
