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
