package co.datachef.http.client.sttp

import sttp.client.SttpBackendOptions
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio._

import scala.concurrent.duration._

object Main extends App {

  private def makeProgram(): RIO[ZEnv, Unit] = {

    val sttpClientLayer =
      AsyncHttpClientZioBackend.layer(SttpBackendOptions(10.seconds, None))

    val program = for {
      result <- ExpointsClient.allCustomers()
      _ <- putStrLn(result.toString)
    } yield ()

    program
      .tapError(error => putStrLn(s"Failing attempt ${error.getMessage}"))
      .provideSomeLayer(Console.live ++ Clock.live ++ sttpClientLayer)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    makeProgram()
      .foldM(
        e => putStrLn(s"Execution failed with: ${e.printStackTrace()}") *> ZIO.succeed(1),
        _ => ZIO.succeed(0)
      )
      .exitCode
}
