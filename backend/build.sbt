name := "backend"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "typesafe release"   at "http://repo.typesafe.com/typesafe/releases/",
  "typesafe repo"      at "http://repo.typesafe.com/typesafe/repo/",
  "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "maven central"      at "http://repo1.maven.org/maven2/"
)

libraryDependencies ++= Seq(
  ws,
  cache,
  filters,
  "org.scalatestplus" %% "play" % "1.1.0" % "test",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0-SNAPSHOT",
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.46.0" % "test"
)