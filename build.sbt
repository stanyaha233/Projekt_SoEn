val scala3Version = "3.3.8"

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
    libraryDependencies += "com.google.inject" % "guice" % "7.0.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0",

    coverageExcludedFiles := ".*Main.*;.*UnoPlay.*;.*SwingGui.*",
    coverageExcludedPackages := "uno\\.gui\\..*;uno\\.aview\\..*",
    Test / parallelExecution := false,
    Compile / mainClass := Some("uno.Main"),
    run / connectInput := true
  )