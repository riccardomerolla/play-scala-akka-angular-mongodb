name := """reactive-web-template"""

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
  "org.webjars" %% "webjars-play" % "2.3-M1",
  "org.webjars" % "bootstrap" % "2.3.1",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

lazy val main = (project in file(".")).addPlugins(PlayScala)
