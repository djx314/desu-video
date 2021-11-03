package desu.models

import io.circe.generic.JsonCodec

@JsonCodec
case class ResultSet[T](data: T)
@JsonCodec
case class RootFilePaths(files: List[String])
