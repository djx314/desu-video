package desu

import com.eclipsesource.v8.{Releasable, V8, V8Object}
import zio._

import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.{Executors => JExecutors}

object AA extends ZIOAppDefault {

  private val execute               = ExecutionContext.fromExecutor(JExecutors.newSingleThreadExecutor())
  def doThread[T](s: => T): Task[T] = ZIO.fromFutureInterrupt(ec => Future(s)(execute))

  def releaseRealeaseable(s: Releasable): UIO[Unit] =
    doThread(s.release()).catchAll(f => ZIO.logErrorCause("Close V8 object error.", Cause.fail(f)))

  def fromReleasable[T <: Releasable](a: => T)(implicit tag: Tag[T]): TaskLayer[T] =
    ZLayer.scoped(ZIO.acquireRelease(doThread(a))(releaseRealeaseable))

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    def script(runtime: V8) = doThread(
      runtime.executeVoidScript(
        ""
          + "var person = {};\n"
          + "var hockeyTeam = {name : 'WolfPack'};\n"
          + "person.first = 'Ian';\n"
          + "person['last'] = 'Bull';\n"
          + "person.hockeyTeam = hockeyTeam;\n"
      )
    )

    val managed: TaskLayer[(V8Object, V8Object, String)] = for {
      runtimeEnv  <- fromReleasable(V8.createV8Runtime())
      _           <- ZLayer.fromZIO(script(runtimeEnv.get[V8]))
      personEnv   <- fromReleasable(runtimeEnv.get[V8].getObject("person"))
      person      <- ZLayer.fromZIO(doThread(personEnv.get[V8Object].getString("first")))
      hockeyTeam  <- fromReleasable(personEnv.get[V8Object].getObject("hockeyTeam"))
      hockeyTeam1 <- fromReleasable(personEnv.get[V8Object].getObject("hockeyTeam"))
    } yield ZEnvironment((hockeyTeam.get[V8Object], hockeyTeam1.get[V8Object], person.get[String]))

    val action = for {
      s     <- ZIO.service[(V8Object, V8Object, String)]
      name1 <- doThread(s._1.getString("name"))
      name2 <- doThread(s._2.getString("name"))
      _     <- Console.printLine(name1)
      _     <- Console.printLine(name2)
      _     <- Console.printLine(s._3)
    } yield {}

    action.provideLayer(managed).catchAll(e => ZIO.logErrorCause(Cause.fail(e))).exitCode
  }

}
