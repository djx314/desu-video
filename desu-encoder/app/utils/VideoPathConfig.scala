package assist.controllers

import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.util.{Timer, TimerTask}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

trait VideoPathConfig {

  val uploadRoot: String

}