package desu

import com.eclipsesource.v8.{Releasable, V8}
import zio._
import logging._

object AA extends App {

  def releaseRealeaseable(s: Releasable): ZIO[Logging, Nothing, Unit] =
    Task(s.release()).catchAll(f => log.throwable("Close V8 object error.", f))
  def fromReleasable[T <: Releasable, R, E](a: ZIO[R, E, T]): ZManaged[R with Logging, E, T] = ZManaged.make(a)(releaseRealeaseable)

  val env =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("my-component")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    def script(runtime: V8) = Task(
      runtime.executeVoidScript(
        ""
          + "var person = {};\n"
          + "var hockeyTeam = {name : 'WolfPack'};\n"
          + "person.first = 'Ian';\n"
          + "person['last'] = 'Bull';\n"
          + "person.hockeyTeam = hockeyTeam;\n"
      )
    )
    val managed = for {
      runtime     <- fromReleasable(Task(V8.createV8Runtime()))
      _           <- ZManaged.fromEffect(script(runtime))
      person      <- fromReleasable(Task(runtime.getObject("person")))
      hockeyTeam  <- fromReleasable(Task(person.getObject("hockeyTeam")))
      hockeyTeam1 <- fromReleasable(Task(person.getObject("hockeyTeam")))
    } yield (hockeyTeam, hockeyTeam1)

    val action = managed.use { case (s1, s2) =>
      for {
        _ <- console.putStrLn(s1.getString("name"))
        _ <- console.putStrLn(s2.getString("name"))
      } yield {}
    }
    action.provideCustomLayer(env).exitCode
  }

}
