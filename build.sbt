name := "dashdoc"

version := "1.0-SNAPSHOT"

resolvers += "JCenter" at "http://jcenter.bintray.com/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.sksamuel.elastic4s" % "elastic4s_2.10" % "1.1.0.0",
  "org.clapper" % "classutil_2.10" % "1.0.4"
)

play.Project.playScalaSettings


