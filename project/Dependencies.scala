import sbt._

object Dependencies {
  val zio = "dev.zio" %% "zio" % "1.0.1"
  val zioLogging = "dev.zio" %% "zio-logging" % "0.5.1"
  val http4sDsl = "org.http4s" %% "http4s-dsl" % "0.21.7"
  val http4sClient = "org.http4s" %% "http4s-blaze-client" % "0.21.7"
  val sttp = "com.softwaremill.sttp.client" %% "core" % "2.2.8"
  val sttpAsyncHttpClientZio = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.2.8"
  val sttpCirce = "com.softwaremill.sttp.client" %% "circe" % "2.2.8"
  val http4sCirce = "org.http4s" %% "http4s-circe" % "0.21.7"
  val fetch = "com.47deg" %% "fetch" % "1.3.0"
  val circe = "io.circe" %% "circe-core" % "0.13.0"
  val circeParser = "io.circe" %% "circe-parser" % "0.13.0"
  val circeGeneric = "io.circe" %% "circe-generic" % "0.13.0"
  val cats = "org.typelevel" %% "cats-core" % "2.1.1"
  val zioInteropCats = "dev.zio" %% "zio-interop-cats" % "2.1.4.0"
}
