package wiring

import play.api._
import zio._

object commons {

  type ConfiguretionHas = Has[Configuration]

  object config {
    val zio: ZIO[ConfiguretionHas, Nothing, Configuration] = ZIO.access[ConfiguretionHas](_.get)
    trait ConfigerLayer {
      val configer: Configuration
      val configLive: ZLayer[Any, Nothing, ConfiguretionHas] = ZLayer.succeed(configer)
    }
  }

  type AppContenxt = ZEnv with ConfiguretionHas
  type HttpContext = ZLayer[Any, Throwable, AppContenxt]
  class HttpContextImpl(override val configer: Configuration) extends config.ConfigerLayer {
    val live: HttpContext = ZEnv.live >+> (configLive)
  }

}
