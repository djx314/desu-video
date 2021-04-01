package desu.video.tapir.config

import desu.video.tapir.mainapp.Layers
import zio.ZIO

import java.nio.file.{Files, Path, Paths}

object AppConfig {

  val rootPath: ZIO[Layers.VideoConf, Throwable, Path] = {
    def validatePath(path: Path) = if (Files.exists(path) && Files.isDirectory(path)) ZIO.succeed(path) else ZIO.fail(new Exception("路径不存在或路径不是文件夹。"))
    for {
      conf  <- ZIO.access[Layers.VideoConf](_.get)
      root  <- ZIO.effect(conf.config.getString("desu.video.file.rootPath"))
      path  <- ZIO.effect(Paths.get(root))
      path1 <- validatePath(path)
    } yield path1
  }

}
