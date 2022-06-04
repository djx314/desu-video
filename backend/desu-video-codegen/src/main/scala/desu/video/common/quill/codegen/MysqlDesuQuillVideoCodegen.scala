package desu.video.common.quill.codegen

import io.getquill.codegen.gen.EmitterSettings
import io.getquill.codegen.jdbc.ComposeableTraitsJdbcCodegen
import io.getquill.codegen.model.{JdbcColumnMeta, JdbcTableMeta, NameParser, SnakeCaseCustomTable}

import java.nio.file.Paths
import java.sql._
import io.getquill.codegen.util.StringUtil._

object MysqlDesuQuillVideoCodegen extends App {
  val url = "jdbc:mysql://localhost:3306/desu_video?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8";
  Class.forName("com.mysql.cj.jdbc.Driver")
  val snakecaseConfig = () => DriverManager.getConnection(url, "root", "root")

  val packageName = "desu.video.common.quill.model"
  val sourcePath  = Paths.get("desu", "video", "common", "quill", "model")

  val gen = new ComposeableTraitsJdbcCodegen(snakecaseConfig, packageName) {
    override def nameParser: NameParser = SnakeCaseCustomTable(s => s.tableName.snakeToLowerCamel)
    override def generatorMaker = new SingleGeneratorFactory[ContextifiedUnitGenerator] {
      override def apply(emitterSettings: EmitterSettings[JdbcTableMeta, JdbcColumnMeta]): ContextifiedUnitGenerator =
        new ContextifiedUnitGeneratorImpl(emitterSettings)
    }

    class ContextifiedUnitGeneratorImpl(emitterSettings: EmitterSettings[JdbcTableMeta, JdbcColumnMeta])
        extends super.ContextifiedUnitGenerator(emitterSettings) {
      override def tableSchemasCode: String = "import io.getquill._\n" + super.tableSchemasCode
    }
    override val renderMembers: Boolean = true
  }

  val sourceRootDir = Paths.get(args(0))
  val genDir        = sourceRootDir.resolve(Paths.get("src", "main", "scala")).resolve(sourcePath)
  gen.writeFiles(genDir.toAbsolutePath.toString)
}
