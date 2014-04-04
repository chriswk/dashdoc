name := "dashdoc"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.sksamuel.elastic4s" % "elastic4s_2.10" % "1.1.0.0",
)

play.Project.playScalaSettings
