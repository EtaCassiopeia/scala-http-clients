package co.datachef.http.client.sttp

import scala.util.control.NoStackTrace

sealed trait Error extends Throwable with NoStackTrace

object Error {
  final case class RequestTimedOut(message: String)
      extends Throwable(s"Timed out handling request. $message")
      with Error

  final case class ExpointsError(message: String) extends Throwable(s"$message") with Error

  final case class ErrorFetchingCustomers(reason: String)
      extends Throwable(s"Error fetching customers  with reason $reason!")
      with Error
}
