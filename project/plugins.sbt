resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "mavenRepoJX" at "http://repo1.maven.org/maven2/",
  //"oschina" at "http://maven.oschina.net/content/groups/public",
  "jgit-repo" at "http://download.eclipse.org/jgit/maven"
)

externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

val playVersion = "2.6.0"
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("com.arpnetworking" % "sbt-typescript" % "0.3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.1")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")
//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
//addSbtPlugin("com.arpnetworking" % "sbt-typescript" % "0.3.4")
//addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")