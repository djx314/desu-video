package desu.video.common.quill.model.desuVideo

case class flyway_schema_history(installedRank: Int, version: Option[String], description: String, `type`: String, script: String, checksum: Option[Int], installedBy: String, installedOn: java.time.LocalDateTime, executionTime: Int, success: Boolean)

case class dir_mapping(id: Int, filePath: String, parentId: Int)

trait DesuVideoExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this:io.getquill.context.Context[Idiom, Naming] =>

  object flyway_schema_historyDao {
      def query = quote {
          querySchema[flyway_schema_history](
            "flyway_schema_history",
            _.installedRank -> "installed_rank",
            _.version -> "version",
            _.description -> "description",
            _.`type` -> "type",
            _.script -> "script",
            _.checksum -> "checksum",
            _.installedBy -> "installed_by",
            _.installedOn -> "installed_on",
            _.executionTime -> "execution_time",
            _.success -> "success"
          )
                    
        }
                  
    }

    object dir_mappingDao {
      def query = quote {
          querySchema[dir_mapping](
            "dir_mapping",
            _.id -> "id",
            _.filePath -> "file_path",
            _.parentId -> "parent_id"
          )
                    
        }
                  
    }
}
