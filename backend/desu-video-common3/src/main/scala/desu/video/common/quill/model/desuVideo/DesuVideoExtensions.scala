package desu.video.common.quill.model.desuVideo

case class dirMapping(id: Int, filePath: String, parentId: Int)

case class flywaySchemaHistory(
  installedRank: Int,
  version: Option[String],
  description: String,
  `type`: String,
  script: String,
  checksum: Option[Int],
  installedBy: String,
  installedOn: java.time.LocalDateTime,
  executionTime: Int,
  success: Boolean
)

import io.getquill._
trait DesuVideoExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object dirMappingDao {
    def query = quote {
      querySchema[dirMapping](
        "dir_mapping",
        _.id       -> "id",
        _.filePath -> "file_path",
        _.parentId -> "parent_id"
      )

    }

  }

  object flywaySchemaHistoryDao {
    def query = quote {
      querySchema[flywaySchemaHistory](
        "flyway_schema_history",
        _.installedRank -> "installed_rank",
        _.version       -> "version",
        _.description   -> "description",
        _.`type`        -> "type",
        _.script        -> "script",
        _.checksum      -> "checksum",
        _.installedBy   -> "installed_by",
        _.installedOn   -> "installed_on",
        _.executionTime -> "execution_time",
        _.success       -> "success"
      )

    }

  }
}
