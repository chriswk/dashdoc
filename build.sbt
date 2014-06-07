name := "dashdoc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "JCenter" at "http://jcenter.bintray.com/"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.sksamuel.elastic4s" %% "elastic4s" % "1.2.0.0",
  "org.clapper" %% "classutil" % "1.0.5",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "typeaheadjs" % "0.10.2",
  "org.webjars" % "jquery" % "1.11.0-1",
  "org.webjars" % "handlebars" % "2.0.0-alpha.2",
  "org.webjars" % "datatables" % "1.10.0-beta-2",
  "org.webjars" % "highlightjs" % "8.0-3",
  "org.apache.commons" % "commons-compress" % "1.8"
)

