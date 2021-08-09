package desu.video.common.model

import io.circe.generic.JsonCodec

@JsonCodec
case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)
}
