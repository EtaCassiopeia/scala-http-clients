package co.datachef.http.client.http4s

import co.datachef.http.client.domain.{Customer, CustomerResponse}
import co.datachef.http.client.http4s.HttpClient.{HttpClient, _}
import io.circe.generic.auto._
import zio._

object ExpointsClient {
  //https://documentation.expoints.nl/external/Help/Api/GET-api-v3-customer
  def allCustomers(): RIO[HttpClient, CustomerResponse] =
    get[CustomerResponse]("/external/api/v3/customer")
}
