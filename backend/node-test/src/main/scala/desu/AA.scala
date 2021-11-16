package desu

import com.eclipsesource.v8.{Releasable, V8}
import zio._
import logging._

object AA extends App {

  def releaseRealeaseable(s: Releasable): ZIO[Logging, Nothing, Unit] =
    Task(s.release()).catchAll(f => log.throwable("Close V8 object error.", f))
  def fromReleasable[T <: Releasable](a: => T): ZManaged[Logging, Throwable, T] = ZManaged.make(Task(a))(releaseRealeaseable)

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
      runtime     <- fromReleasable(V8.createV8Runtime())
      _           <- ZManaged.fromEffect(script(runtime))
      person      <- fromReleasable(runtime.getObject("person"))
      hockeyTeam  <- fromReleasable(person.getObject("hockeyTeam"))
      hockeyTeam1 <- fromReleasable(person.getObject("hockeyTeam"))
    } yield (hockeyTeam, hockeyTeam1, person.getString("first"))

    val action = managed.use { case (s1, s2, ian) =>
      for {
        _ <- console.putStrLn(s1.getString("name"))
        _ <- console.putStrLn(s2.getString("name"))
        _ <- console.putStrLn(ian)
      } yield {}
    }
    action.provideCustomLayer(env).exitCode
  }

}
