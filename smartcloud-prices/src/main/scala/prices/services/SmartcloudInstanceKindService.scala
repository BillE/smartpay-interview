package prices.services

import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.headers.{Accept, Authorization}
import prices.data._

object SmartcloudInstanceKindService {

  final case class Config(
      baseUri: String,
      token: String
  )

  def make[F[_]: Concurrent](config: Config, client: Client[F]): InstanceKindService[F] = new SmartcloudInstanceKindService(config, client)

  private final class SmartcloudInstanceKindService[F[_]: Concurrent](
      config: Config,
      client: Client[F]
  ) extends InstanceKindService[F] {

    implicit val instanceKindsEntityDecoder: EntityDecoder[F, List[String]] = jsonOf[F, List[String]]

    val getAllUri = s"${config.baseUri}/instances"

    private val request: Request[F] =
      Request[F](
        uri = Uri.unsafeFromString(getAllUri),
        headers = Headers(Accept(MediaType.text.strings), Authorization(Credentials.Token(AuthScheme.Bearer, config.token)))
      )

    override def getAll(): F[List[InstanceKind]] =
      client.run(request).use { response =>
        if (response.status.isSuccess) {
          response.as[List[String]].map(strings => strings.map(s => InstanceKind(s)))
        } else {
          Concurrent[F].raiseError(new Exception("Error fetching results."))
        }
      }

  }

}
