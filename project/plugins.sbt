resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "mavenRepoJX" at "http://repo1.maven.org/maven2/"
  //"oschina" at "http://maven.oschina.net/content/groups/public",
  //"jgit-repo" at "http://download.eclipse.org/jgit/maven"
)

//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

val playVersion = "2.6.11"
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % playVersion)
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")