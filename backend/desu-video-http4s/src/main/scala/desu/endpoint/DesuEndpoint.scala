package desu.endpoint

import desu.config.AppConfig

object DesuEndpoint:

  /*implicit class appendOutPout[A, I, E, O, -R](endpo: Endpoint[A, I, E, ResultSet[O], R]) {
    def append: Endpoint[A, I, E, (O, StatusCode), R] =
      endpo.out(statusCode).mapOut(d => (d._1.data, d._2))(s => (ResultSet(s._1, s._2.code), s._2))
    def appendSuccess: Endpoint[A, I, E, O, R] =
      endpo.out(statusCode(StatusCode.Ok)).mapOut(d => d.data)(s => ResultSet(s, StatusCode.Ok.code))
  }

  implicit class appendErrorOutPout[A, I, E, O, -R](endpo: Endpoint[A, I, ResultSet[E], O, R]) {
    def appendError: Endpoint[A, I, (E, StatusCode), O, R] =
      endpo.errorOut(statusCode).mapErrorOut(d => (d._1.data, d._2))(s => (ResultSet(s._1, s._2.code), s._2))
  }*/

  case class InputFileName(fileName: String) derives io.circe.Encoder.AsObject

  /*val baiduPageEndpoint = AppConfig.filePageRoot.in("baiduPage").out(htmlBodyUtf8).errorOut(htmlBodyUtf8)

  val rootPathFileEndpoint =
    AppConfig.filePageRoot.post
      .in("rootPathFile")
      .in(jsonBody[InputFileName])
      .out(jsonBody[ResultSet[DirInfo]])
      .appendSuccess
      .errorOut(jsonBody[ResultSet[String]])
      .appendError

  val rootPathFilesEndpoint =
    AppConfig.filePageRoot
      .in("rootPathFiles")
      .out(jsonBody[ResultSet[DirInfo]])
      .appendSuccess
      .errorOut(jsonBody[ResultSet[String]])
      .appendError*/

end DesuEndpoint
