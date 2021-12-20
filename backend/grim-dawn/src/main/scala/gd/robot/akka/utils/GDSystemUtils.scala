package gd.robot.akka.utils

import akka.actor.typed.{ActorRef, ActorSystem, DispatcherSelector}
import akka.util.Timeout
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.gdactor.gohome.systemactor.WaitForGDFocus

import java.nio.file.{Files, Path, Paths}
import java.util.{Date, Optional}
import scala.concurrent.Future
import scala.io.Source

class GDSystemUtils(system: ActorSystem[WebAppListener.GoHomeKey]) {
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.scheduler
  val currentProcessIdExeFileName       = "isGDRunning.exe"

  private def currentProcessIdExeImpl: Future[Path] = {
    val tempPath = Paths.get(System.getProperty("java.io.tmpdir"))
    Future {
      val path           = Files.createDirectories(tempPath.resolve(s"gdSystemUtil-${new Date().getTime / 1000 / 60 / 60 / 24}"))
      val isRunningBytes = ImageUtils.getBytesFromClasspath(s"/$currentProcessIdExeFileName")
      Files.write(path.resolve(currentProcessIdExeFileName), isRunningBytes)
      path
    }(blockExecutionContext)
  }
  val currentProcessIdExe: Future[Path] = currentProcessIdExeImpl

  def fromOptional[T](o: Optional[T]): Option[T] = o.map(Option.apply[T]).orElse(Option.empty)

  def getCurrentProcessName: Future[Option[String]] = {
    def process(context: Path): Process = Runtime.getRuntime.exec(s"cmd.exe /C $currentProcessIdExeFileName", Array.empty, context.toFile)
    def nameFromProcessId(processId: Long): java.util.Optional[String] = for {
      process <- ProcessHandle.of(processId)
      command <- process.info().command()
    } yield Paths.get(command).getFileName.toString

    for {
      exePath       <- currentProcessIdExe
      processResult <- Future(process(exePath))(blockExecutionContext)
      result        <- Future(Source.fromInputStream(processResult.getInputStream).getLines().mkString.trim)(blockExecutionContext)
      processIdOpt  <- Future(result.toLongOption)
      processName <- Future {
        for (processId <- processIdOpt) yield nameFromProcessId(processId)
      }(blockExecutionContext)
    } yield for {
      nameOpt <- processName
      name    <- fromOptional(nameOpt)
    } yield name
  }

  // val 开始菜单图标Byte     = ImageUtils.getBytesFromClasspath("/窗口定位/开始菜单栏图标.png")
  val 恐怖黎明拾落选择按钮Byte = ImageUtils.getBytesFromClasspath("/窗口定位/恐怖黎明拾落选择按钮.png")

  val waitForGDFocus: ActorRef[WaitForGDFocus.ActionStatus] = system.systemActorOf(WaitForGDFocus(), "wait-for-gd-focus-actor")
  waitForGDFocus ! WaitForGDFocus.CheckGDFocus(false)

  def isNowOnFocus: Future[Boolean] = for (name <- getCurrentProcessName) yield name == Some("Grim Dawn.exe")

  import akka.actor.typed.scaladsl.AskPattern._
  import scala.concurrent.duration._
  implicit val timeout = Timeout(1000.hours)

  def waitForFocus[T](f: => Future[T]): Future[T] = {
    val future = waitForGDFocus ? ((actor: ActorRef[Boolean]) => WaitForGDFocus.InputPromise(actor))
    future.flatMap((_: Boolean) => f)
  }

  def waitForFocus: Future[Unit] = {
    val future = waitForGDFocus ? ((actor: ActorRef[Boolean]) => WaitForGDFocus.InputPromise(actor))
    future.map((_: Boolean) => ())
  }

}
