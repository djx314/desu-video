package org.xarcher.sbt

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import sbt._
import sbt.Keys._

import scala.concurrent.Future

object Helpers {

  implicit class implicitProjectToPlay(project: Project) {

    def toPlay(filePath: String): Project = {

      (project in file(filePath))
        .enablePlugins(play.sbt.PlayScala)
        .settings(CustomSettings.customSettings: _*)

    }

  }

  sealed trait Message {
    val mess: String
    val time: Date
  }
  case class RightMess(override val mess: String, override val time: Date) extends Message {
    override def toString = s"right: $mess"
  }
  case class ErrorMess(override val mess: String, override val time: Date) extends Message {
    override def toString = s"error: $mess"
  }

  def processGen(process: java.lang.Process): Future[List[Message]] = {
    Future.sequence(List(
      listen(process.getInputStream(), s => RightMess(s, new Date())),
      listen(process.getErrorStream(), s => ErrorMess(s, new Date()))
    )).map { s =>
      s.flatten.sortBy(_.time)
    }
  }

  def listen(s: InputStream, result: String => Message): Future[List[Message]] = Future {
    val inputReader = new InputStreamReader(s)
    val inputBuReader = new BufferedReader(inputReader)
    try {
      (Iterator continually inputBuReader.readLine takeWhile (_ != null) map { t =>
        val returnInfo = result(t)
        println(returnInfo)
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

  def execCommonLine(process: java.lang.Process) = {
    processGen(process)//.map(s => println(s.mkString("\n")))
    println(process.waitFor)
  }

  def copyFiles(root: File, prefix: String): List[(File, String)] = {
    root.listFiles().toList.flatMap { file =>
      if (file.isDirectory) {
        copyFiles(file, prefix + "/" + file.getName)
      } else {
        List(file -> (prefix + "/" + file.getName))
      }
    }
  }

}