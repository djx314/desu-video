import org.xarcher.sbt.CustomSettings
import org.xarcher.sbt.Dependencies
import org.xarcher.sbt.Helpers._

import sbt._
import sbt.Keys._

val printlnDo = println("""
|   __     __     __
|  / /    / /    / /
| / /_   / /_   / /_
|| '_ \ | '_ \ | '_ \
|| (_) || (_) || (_) |
| \___/  \___/  \___/
""".stripMargin
)

lazy val playVersion = play.core.PlayVersion.current

CustomSettings.commonProjectSettings

transitiveClassifiers in ThisBuild := Seq("sources", "jar", "javadoc")

dependsOn(encoder)

lazy val encoder = (project in file("./desu-encoder"))
  .settings(CustomSettings.customSettings: _*)
  .dependsOn(playCirce, model)
.aggregate(playCirce, model)

/*lazy val assets: Project = (project in file("./desu-assets"))
  .settings(CustomSettings.customSettings: _*)
  .dependsOn(playCirce)
  .dependsOn(model)*/


lazy val model = (project in file("./desu-model"))
  .settings(CustomSettings.commonProjectSettings: _*)

lazy val playCirce = (project in file("./play-circe"))
.settings(CustomSettings.commonProjectSettings: _*)