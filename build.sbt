name := "dashdoc"

version := "1.0-SNAPSHOT"

resolvers += "JCenter" at "http://jcenter.bintray.com/"

val akkaVersion = "2.2.0"

dependencyOverrides += "com.typesafe.akka" %% "akka-actor" % akkaVersion

dependencyOverrides += "com.typesafe.akka" %% "akka-agent" % akkaVersion

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.sksamuel.elastic4s" %% "elastic4s" % "1.1.0.0",
  "org.clapper" %% "classutil" % "1.0.4",
  "com.beachape.filemanagement" %% "schwatcher" % "0.0.9",
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "typeaheadjs" % "0.10.2",
  "org.webjars" % "jquery" % "1.11.0-1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test"
)

play.Project.playScalaSettings


