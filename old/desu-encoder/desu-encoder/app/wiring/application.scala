package wiring

import net.scalax.mp4.encoder.CurrentEncode
import play.api._
import zio._

object commons {

  type CurrentEncodeHas = Has[CurrentEncode]
  type ConfiguretionHas = Has[Configuration]
  object currentEncode {
    val zio: ZIO[CurrentEncodeHas, Nothing, CurrentEncode] = ZIO.access[CurrentEncodeHas](_.get)
    class CurrentEncodeImpl extends CurrentEncode
    val live: ZLayer[Any, Nothing, CurrentEncodeHas] = ZLayer.succeed(new CurrentEncodeImpl)
  }

  object config {
    val zio: ZIO[ConfiguretionHas, Nothing, Configuration] = ZIO.access[ConfiguretionHas](_.get)
    trait ConfigerLayer {
      val configer: Configuration
      val configLive: ZLayer[Any, Nothing, ConfiguretionHas] = ZLayer.succeed(configer)
    }
  }

  type AppContenxt = ZEnv with CurrentEncodeHas with ConfiguretionHas
  type HttpContext = ZLayer[Any, Throwable, AppContenxt]
  class HttpContextImpl(override val configer: Configuration) extends config.ConfigerLayer {
    val live: HttpContext = ZEnv.live >+> (currentEncode.live ++ configLive)
  }

}
