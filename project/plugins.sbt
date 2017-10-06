resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "mavenRepoJX" at "http://repo1.maven.org/maven2/",
  //"oschina" at "http://maven.oschina.net/content/groups/public",
  "jgit-repo" at "http://download.eclipse.org/jgit/maven"
)

externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

val playVersion = "2.6.6"
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")
//addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")