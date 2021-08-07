package desu.mainapp

import zio._

object common {

  type AppContext    = Any
  type AppEnv        = AppContext with zio.clock.Clock with zio.blocking.Blocking
  type ZIOEnvTask[I] = RIO[AppEnv, I]
  type ZLayerTask[I] = RIO[AppContext, I]

  val applicationContenxtlive: RLayer[ZEnv, ZEnv] = ZLayer.identity[ZEnv]

}
