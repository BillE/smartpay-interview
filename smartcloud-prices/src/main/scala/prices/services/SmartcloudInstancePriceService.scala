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

    // TODO: this should come from the GET call /instances/sc2-micro
    val getAllUri = s"${config.baseUri}/instances/sc2-micro"

    private val request: Request[F] =
      Request[F](
        uri = Uri.unsafeFromString(getAllUri),
        headers = Headers(Accept(MediaType.text.strings), Authorization(Credentials.Token(AuthScheme.Bearer, config.token)))
      )

    override def getPrice(kind: String): F[Price] =
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
