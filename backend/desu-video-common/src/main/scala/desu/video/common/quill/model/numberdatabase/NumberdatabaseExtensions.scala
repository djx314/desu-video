package desu.video.common.quill.model.numberdatabase

case class countSet(id: Int, countSet: String, secondStart: Int, firstStart: Int, isLimited: Boolean)

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

case class countPlan(
  id: Int,
  firstOuterName: String,
  firstOuterType: String,
  firstInnerName: String,
  firstInnerType: String,
  firstStart: Int,
  secondOuterName: String,
  secondOuterType: String,
  secondInnerName: String,
  secondInnerType: String,
  secondStart: Int,
  counterResultId: Option[Int]
)

trait NumberdatabaseExtensions[Idiom <: io.getquill.idiom.Idiom, Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object countSetDao {
    def query = quote {
      querySchema[countSet](
        "count_set",
        _.id          -> "id",
        _.countSet    -> "count_set",
        _.secondStart -> "second_start",
        _.firstStart  -> "first_start",
        _.isLimited   -> "is_limited"
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

  object countPlanDao {
    def query = quote {
      querySchema[countPlan](
        "count_plan",
        _.id              -> "id",
        _.firstOuterName  -> "first_outer_name",
        _.firstOuterType  -> "first_outer_type",
        _.firstInnerName  -> "first_inner_name",
        _.firstInnerType  -> "first_inner_type",
        _.firstStart      -> "first_start",
        _.secondOuterName -> "second_outer_name",
        _.secondOuterType -> "second_outer_type",
        _.secondInnerName -> "second_inner_name",
        _.secondInnerType -> "second_inner_type",
        _.secondStart     -> "second_start",
        _.counterResultId -> "counter_result_id"
      )

    }

  }
}
