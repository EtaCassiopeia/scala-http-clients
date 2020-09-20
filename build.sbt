import Dependencies._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "co.datachef"
ThisBuild / organizationName := "datachef"

lazy val root = (project in file("."))
  .settings(
    name := "scala-http-clients",
    libraryDependencies ++= Seq(
      zio,
      zioLogging,
      http4sClient,
      http4sDsl,
      http4sCirce,
      sttp,
      sttpAsyncHttpClientZio,
      sttpCirce,
      circe,
      circeGeneric,
      circeParser,
      cats,
      zioInteropCats
    )
  )
