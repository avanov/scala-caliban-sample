import Dependencies._

ThisBuild / scalaVersion     := "3.2.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"
ThisBuild / scalacOptions ++= Seq(
    "-deprecation",
    "-Werror"
)

lazy val root = (project in file("."))
    .settings(
        name := "caliban-sample",
        libraryDependencies ++= Seq
            (   caliban
            ,   calibanFederation
            ,   zioQuery

            //,   circeParser % Test
            ,   zioTest     % Test
            ,   zioTestSbt  % Test
        )
        , scalacOptions ++= Seq("-Xmax-inlines", "128")
        // needed for automatic test picking by SBT
        , testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )
