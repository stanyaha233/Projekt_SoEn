val scala3Version = "3.3.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Projekt_Uno",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,


    libraryDependencies += "org.scalameta" %% "munit" % "1.2.4" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",

    coverageExcludedFiles := ".*Main.*;.*UnoPlay.*;.*SwingGui.*",
    coverageExcludedPackages := "uno\\.gui\\..*;uno\\.aview\\..*",
    Test / parallelExecution := false
  )