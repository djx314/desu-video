package desu.video.common.model

import io.circe.Codec

case class DesuResult[T](isSucceed: Boolean, data: T, message: String) derives Codec.AsObject

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)
}
