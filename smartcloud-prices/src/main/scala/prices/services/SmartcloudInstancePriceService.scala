package prices.services

import cats.effect.Concurrent
import org.http4s.{AuthScheme, Credentials, EntityDecoder, Headers, MediaType, Request, Uri}
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.headers.{Accept, Authorization}

object SmartcloudInstancePriceService {


  final case class Config(
                           baseUri: String,
                           token: String
                         )

  def make[F[_]: Concurrent](config: Config, client: Client[F]): InstancePriceService[F] = new SmartcloudInstancePriceService(config, client)

  private final class SmartcloudInstancePriceService[F[_]: Concurrent](
                                                                       config: Config,
                                                                       client: Client[F]
                                                                     ) extends InstancePriceService[F] {

    implicit val instancePriceEntityDecoder: EntityDecoder[F, Price] = jsonOf[F, Price]

    val getAllUri = s"${config.baseUri}/instances"

    override def getPrice(kind: String): F[Price] = {
      val request = Request[F](
        uri = Uri.unsafeFromString(getAllUri) / kind ,
        headers = Headers(Accept(MediaType.application.json), Authorization(Credentials.Token(AuthScheme.Bearer, config.token)))
      )
      client.run(request).use { response =>
        if (response.status.isSuccess) {
          response.as[Price]
        } else if (response.status.code.equals(429)) {
          Concurrent[F].raiseError(new Exception("Too many request, please try again later."))
        } else {
          Concurrent[F].raiseError(new Exception("An error has occurred fetching results."))
        }
      }
    }

  }

}
