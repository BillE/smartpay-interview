package prices.routes

import cats.implicits._
import prices.services.InstancePriceService
import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import prices.routes.protocol.{InstancePriceResponse}

case class InstancePriceRoutes[F[_]: Sync](instancePriceService: InstancePriceService[F]) extends Http4sDsl[F] {

  val prefix = "/prices"

  implicit val instancePriceResponseEncoder = jsonEncoderOf[F, InstancePriceResponse]

  object KindQueryParamMatcher extends QueryParamDecoderMatcher[String]("kind")

  private val get: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root :? KindQueryParamMatcher(kind) =>
      instancePriceService.getPrice(kind).flatMap(price => Ok(InstancePriceResponse(price)))
  }

  def routes: HttpRoutes[F] =
    Router(
      prefix -> get
    )
}
