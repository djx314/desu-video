package desu.models

import io.circe.generic.JsonCodec

@JsonCodec
case class RootPathFiles(files: List[String])

@JsonCodec
case class DirId(id: Long, fileName: String)

case class FileNotConfirmException(message: String) extends Exception(message)

@JsonCodec
case class RootFileNameRequest(fileName: String)

case class DesuConfig(desu: VideoConfig, mysqlDesuQuillDB: MysqlDesuQuillDB)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)

case class MysqlDesuQuillDB(dataSource: DesuDataSource)
case class DesuDataSource(driverClassName: String, jdbcUrl: String, username: String, password: String)
