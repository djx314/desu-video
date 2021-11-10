package desu.video.common.quill.codegen

import io.getquill.codegen.jdbc.ComposeableTraitsJdbcCodegen
import io.getquill.codegen.model.{NameParser, SnakeCaseCustomTable}

import java.nio.file.Paths
import java.sql._
import io.getquill.codegen.util.StringUtil._

object MysqlDesuQuillVideoCodegen extends App {
  val url = "jdbc:mysql://localhost:3306/desu_video?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8";
  Class.forName("com.mysql.cj.jdbc.Driver")
  def conn            = DriverManager.getConnection(url, "root", "root")
  val snakecaseConfig = () => conn

  val packageName = "desu.video.common.quill.model"
  val sourcePath  = "desu/video/common/quill/model"

  val gen = new ComposeableTraitsJdbcCodegen(snakecaseConfig, packageName) {
    override def nameParser: NameParser = SnakeCaseCustomTable(s => s.tableName.snakeToLowerCamel)
    override val renderMembers: Boolean = true
  }

  val sourceRootDir = Paths.get(args(0))
  val genDir        = sourceRootDir.resolve(Paths.get("src", "main", "scala")).resolve(sourcePath)
  gen.writeFiles(genDir.toAbsolutePath.toString)
}
