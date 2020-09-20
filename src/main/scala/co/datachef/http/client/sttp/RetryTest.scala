package co.datachef.http.client.sttp

import zio.console.putStrLn
import zio.duration.durationInt
import zio._

sealed trait InternalError {
  def message: String
}

case class TooManyRequests(message: String) extends InternalError

case class OtherErrors(message: String) extends InternalError

object RetryTest extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    makeProgram()
      .foldM(
        e => putStrLn(s"Execution failed ${e.message}") *> ZIO.succeed(1),
        _ => ZIO.succeed(0)
      )
      .exitCode

  private def getCustomers(n: Int): IO[InternalError, String] = {
    if (n > 20) ZIO.succeed("Running")
    else if (n < 3) ZIO.fail(OtherErrors("Failed"))
    else ZIO.fail(TooManyRequests("Too many requests received"))
  }

  private def makeProgram() = {
    val scheduler = Schedule.exponential(50.millis) && (Schedule.recurs(5) && Schedule.recurWhile[InternalError] {
      case TooManyRequests(_) => false
      case _                  => true
    })

    for {
      ref <- Ref.make(0)
      _ <- (
          for {
            r <- ref.get
            result <- putStrLn(s"Getting customer $r") *> getCustomers(r).tapError(_ =>
              putStrLn("Updating ref") *> ref.update(_ + 1)
            )
          } yield result
      )
        .retry(scheduler)
    } yield ()
  }
}
