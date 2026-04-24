val scala3Version = "3.8.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Projekt_Uno",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.2.4" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,

    coverageExcludedFiles := ".*Main.*;.*Test.*"
  )