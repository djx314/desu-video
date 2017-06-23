package net.scalax.mp4.encoder

import java.io.File

import scala.concurrent.Future

trait EncoderAbs {

  val encodeType: String

  def encode(sourceRoot: File, sourceFiles: List[File], targetRoot: File): Future[List[File]]

}