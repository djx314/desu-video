package net.scalax.mp4.encoder

import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.util.{Timer, TimerTask}

import scala.concurrent.{Future, Promise}

object EncodeHelper {

  implicit val ec = Execution.multiThread

  def processGen(process: java.lang.Process): Future[List[String]] = {
    Future.sequence(List(
      listen(process.getInputStream(), { s =>
        println(s"命令行输出(正确):$s")
        s
      }),
      listen(process.getErrorStream(), { s =>
        println(s"命令行输出(错误):$s")
        s
      })
    )).map { s =>
      s.flatten
    }
  }

  def listen[T](s: InputStream, result: String => T): Future[List[T]] = Future {
    val inputReader = new InputStreamReader(s)
    val inputBuReader = new BufferedReader(inputReader)
    try {
      (Iterator continually inputBuReader.readLine takeWhile (_ != null) map { t =>
        val returnInfo = result(t)
        returnInfo
      }).toList
    } catch {
      case e: Exception =>
        e.printStackTrace
        throw e
    } finally {
      inputBuReader.close()
      inputReader.close()
      s.close()
    }
  }

  def execCommand(command: String): Future[List[String]] = {
    val runtime = Runtime.getRuntime
    println(s"exec: $command")
    processGen(runtime.exec(command))
  }

  def windowsWaitTargetFileFinishedEncode(targetFile: File): Future[Boolean] = {
    val isSuccess = if (targetFile.exists())
      try {
        targetFile.renameTo(new File(targetFile.getParentFile, targetFile.getName))
        true
      } catch {
        case e: Exception =>
          println("未完成转码")
          false
      }
    else
      false

    if (isSuccess) {
      Future successful isSuccess
    } else {
      val isSuccessF = Promise[Future[Boolean]]()
      val resultF = isSuccessF.future
      val timer = new Timer()
      timer.schedule(new TimerTask() {
        override def run(): Unit = {
          isSuccessF.success(windowsWaitTargetFileFinishedEncode(targetFile))
        }
      }, 2000) // 指定延迟2000毫秒后执行
      resultF.flatMap(identity)
    }

  }

}