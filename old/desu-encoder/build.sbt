import org.scalax.sbt.CustomSettings

CustomSettings.commonProjectSettings

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("drun", "encoder/run 2333")

val model   = project in file(".") / "desu-model"
val encoder = (project in file(".") / "desu-encoder").dependsOn(model).aggregate(model)

dependsOn(encoder)

/*lazy val assets: Project = (project in file("./desu-assets"))
  .settings(CustomSettings.customSettings: _*)
  .dependsOn(playCirce)
  .dependsOn(model)*/
