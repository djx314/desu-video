package net.scalax.mp4.encoder

import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.util.{Timer, TimerTask}

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future, Promise}

object EncodeHelper {

  val logger = LoggerFactory.getLogger("Exec Factory")

  def processGen(process: java.lang.Process)(implicit ec: ExecutionContext): Future[List[String]] = {
    val waitForF = Future { process.waitFor }
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

  def listen[T](s: InputStream, result: String => T)(implicit ec: ExecutionContext): Future[List[T]] = Future {
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

  def execCommand(command: String)(implicit ec: ExecutionContext): Future[List[String]] = {
    val runtime = Runtime.getRuntime
    println(s"exec: $command")
    processGen(runtime.exec(command))
  }

  @deprecated
  def execWithDir(commands: List[String], dir: File)(implicit ec: ExecutionContext): Future[List[String]] = {
    val pros = new ProcessBuilder(scala.collection.JavaConverters.seqAsJavaListConverter(commands).asJava)
    pros.directory(dir)
    println(s"exec: ${pros.command().toArray.mkString(" ")}")
    processGen(pros.start())
  }

  def execWithPath(commands: List[String], dir: File, successGen: String => Unit = { _ => () }, failGen: String => Unit = { _ => () })(implicit ec: ExecutionContext): Future[Unit] = {
    val pros = new ProcessBuilder(scala.collection.JavaConverters.seqAsJavaListConverter(commands).asJava)
    pros.directory(dir)
    logger.info(s"exec: ${pros.command().toArray.mkString(" ")}\ndir: ${dir.getCanonicalPath}")

    val proccess = pros.start()
    val resultF = Future.sequence(List(
      listen(proccess.getInputStream(), { s =>
        val result = successGen(s)
        result
      }),
      listen(proccess.getErrorStream(), { s =>
        val result = failGen(s)
        result
      })
    )).map { s =>
      s.flatten
    }
    val waitForF = Future { proccess.waitFor }
    println("等待命令行输出")
    resultF.map { (_: List[Unit]) => () }
  }

  def windowsWaitTargetFileFinishedEncode(targetFile: File)(implicit ec: ExecutionContext): Future[Boolean] = {
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
      val isSuccessF = Promise[Future[Boolean]]
      val resultF = isSuccessF.future
      val timer = new Timer()
      timer.schedule(new TimerTask() {
        override def run(): Unit = {
          isSuccessF.success(windowsWaitTargetFileFinishedEncode(targetFile))
          timer.cancel()
        }
      }, 2000) // 指定延迟2000毫秒后执行
      resultF.flatMap(identity)
    }

  }

}