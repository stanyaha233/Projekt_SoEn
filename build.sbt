val scala3Version = "3.3.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Projekt_Uno",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.17",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test,

    coverageExcludedFiles := ".*Main.*;.*Test.*;.*UnoPlay.*",

    Test / parallelExecution := false,

    testFrameworks += new TestFramework("org.scalatest.tools.Framework")
  )