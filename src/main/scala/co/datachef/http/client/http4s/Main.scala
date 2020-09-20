package co.datachef.http.client.http4s

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import zio._
import zio.console.{Console, putStrLn}
import zio.duration.durationInt
import zio.interop.catz._
import zio.logging.Logging
import zio.clock.Clock

import scala.concurrent.ExecutionContext.Implicits

object Main extends App {

  override def run(args: List[String]): URIO[zio.ZEnv with Console, ExitCode] = {
    val program = for {
      http4sClient <- makeHttpClient
      _ <- makeProgram(http4sClient)
    } yield ()

    program
      .foldM(
        e => putStrLn(s"Execution failed with: ${e.printStackTrace()}") *> ZIO.succeed(1),
        _ => ZIO.succeed(0)
      )
      .exitCode
  }

  private def makeHttpClient: UIO[TaskManaged[Client[Task]]] =
    ZIO.runtime[Any].map { implicit rts =>
      BlazeClientBuilder
        .apply[Task](Implicits.global)
        .resource
        .toManaged
    }

  private def makeProgram(http4sClient: TaskManaged[Client[Task]]): RIO[ZEnv, Unit] = {
    val loggerLayer = Logging
      .console()

    val httpClientLayer = http4sClient.toLayer.orDie
    val http4sClientLayer = (loggerLayer ++ httpClientLayer) >>> HttpClient.http4s ++ Clock.live

    val program = for {
      result <- ExpointsClient.allCustomers()
      _ <- putStrLn(result.toString)
    } yield ()

    program
      .tapError(error => putStrLn(s"Failing attempt ${error.getMessage}"))
      .retry(Schedule.recurs(5) && Schedule.exponential(1.second))
      .provideSomeLayer(http4sClientLayer)
  }
}
