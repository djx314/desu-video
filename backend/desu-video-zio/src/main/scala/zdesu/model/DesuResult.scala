package zdesu.model

import tethys._
import tethys.jackson._
import tethys.derivation.semiauto._

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  implicit def jWriter[T](implicit w: JsonWriter[T]): JsonObjectWriter[DesuResult[T]] = jsonWriter
  implicit def jReader[T](implicit r: JsonReader[T]): JsonReader[DesuResult[T]]       = jsonReader
}
