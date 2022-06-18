package zdesu.model

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  implicit def codec[T](implicit c: JsonValueCodec[T]): JsonValueCodec[DesuResult[T]] = JsonCodecMaker.make
}
