package co.datachef.http.client.http4s

import io.circe.Decoder
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import zio._
import zio.interop.catz._

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    protected val baseUrl = "https://[customer].expoints.nl"

    def get[T](uri: String, parameters: Map[String, String])(implicit d: Decoder[T]): Task[T]
  }

  private[http4s] final class Live(client: Client[Task]) extends HttpClient.Service with Http4sClientDsl[Task] {

    def get[T](resource: String, parameters: Map[String, String])(implicit d: Decoder[T]): Task[T] = {
      val uri = Uri(path = baseUrl + resource).withQueryParams(parameters)

      client.expect[T](uri.toString())
    }
  }

  def http4s: ZLayer[Has[Client[Task]], Nothing, HttpClient] =
    ZLayer.fromService[Client[Task], Service] { http4sClient =>
      new Live(http4sClient)
    }

  def get[T](resource: String)(implicit d: Decoder[T]): RIO[HttpClient, T] =
    RIO.accessM[HttpClient](_.get.get[T](s"$resource", Map()))

  def get[T](resource: String, id: Long)(implicit d: Decoder[T]): RIO[HttpClient, T] =
    RIO.accessM[HttpClient](_.get.get[T](s"$resource/$id", Map()))

  def get[T](resource: String, parameters: Map[String, String] = Map())(implicit
    d: Decoder[T]
  ): RIO[HttpClient, List[T]] =
    RIO.accessM[HttpClient](_.get.get[List[T]](resource, parameters))

}
