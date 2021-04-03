package desu.video.tapir.mainapp

import com.typesafe.config.{Config, ConfigFactory}
import zio._

object Layers {

  type AppContext    = ZEnv with VideoConf
  type VideoConf     = Has[conf.Conf]
  type ZIOEnvTask[I] = RIO[AppContext with clock.Clock, I]
  type LayersTask[I] = RIO[AppContext, I]

  object conf {
    case class Conf(config: Config)
    val layer = ZLayer.fromEffect(ZIO.effect(Conf(ConfigFactory.load())))
  }

  val appLayer: ZLayer[Any, Throwable, AppContext] = ZEnv.live ++ conf.layer

}
