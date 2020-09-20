package co.datachef.http.client.sttp

import co.datachef.http.client.domain.CustomerResponse
import co.datachef.http.client.sttp.Error.{
  ErrorFetchingCustomers,
  GenericExpointsError,
  RequestTimedOut,
  TooManyRequests
}
import io.circe.generic.auto._
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.basicRequest
import sttp.client.circe.asJson
import sttp.model.{StatusCode, Uri}
import zio.clock.Clock
import zio.duration.durationInt
import zio.{Schedule, ZIO}

object ExpointsClient {
  type ClientEnv = SttpClient with Clock

  private val baseUrl = Uri("https", "[customer].expoints.nl")
  private val scheduler = Schedule.exponential(50.millis) && (Schedule.recurs(5)
    && Schedule.recurWhile[Error] {
      case TooManyRequests(_) => false
      case _                  => true
    })

  //https://documentation.expoints.nl/external/Help/Api/GET-api-v3-customer
  def allCustomers(nextCursor: Option[String]): ZIO[ClientEnv, Error, CustomerResponse] = {

    //TODO apply NextCursor
    val request = basicRequest
      .response(asJson[CustomerResponse])
      .get(baseUrl.path("/external/api/v3/customer"))

    SttpClient
      .send(request)
      .timeoutFail(RequestTimedOut(s"Request to get list of customers"))(30.seconds)
      .reject {
        case r if r.code == StatusCode.TooManyRequests => TooManyRequests(r.toString())
      }
      .map(_.body)
      .absolve
      .bimap(err => ErrorFetchingCustomers(err.getMessage), identity)
      .retry(scheduler)

  }
}
