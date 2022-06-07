package desu.video.common.slick.codegen

import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import MySQLProfile.api._
import desu.video.common.slick.DesuDatabase

import java.nio.file.Paths
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{blocking, Await, Future}

object MysqlDesuVideoCodegen extends App {
  val db = new DesuDatabase().db
  // fetch data model
  val modelAction = MySQLProfile.createModel(Some(MySQLProfile.defaultTables)) // you can filter specific tables here
  val modelFuture = db.run(modelAction)
  val codegenFuture = modelFuture.map(model =>
    new SourceCodeGenerator(model) {
      override def Table = new Table(_) {
        override def hugeClassEnabled = true
        override def Column = new Column(_) {
          // use the data model member of this column to change the Scala type,
          // e.g. to a custom enum or anything else
          override def rawType: String =
            if (model.name == "SOME_SPECIAL_COLUMN_NAME") "MyCustomType" else super.rawType
        }
      }
    }
  )

  val sourceRootDir = Paths.get(args(0))
  val genDir        = sourceRootDir.resolve(Paths.get("src", "main", "scala"))
  Await.result(
    codegenFuture.flatMap(codegen =>
      Future {
        blocking(
          codegen.writeToMultipleFiles(
            profile = "slick.jdbc.MySQLProfile",
            folder = genDir.toAbsolutePath.toString,
            pkg = "desu.video.common.slick.model",
            container = "Tables"
          )
        )
      }
    ),
    scala.concurrent.duration.Duration.Inf
  )
}
