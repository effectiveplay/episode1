name := """episode1"""

organization := "com.effectiveplay"

homepage := Some(url("https://effectiveplay.com"))

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.scalatestplus" %% "play" % "1.1.0" % "test"
)
