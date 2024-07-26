package prices.routes.protocol

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import prices.services.Price

final case class InstancePriceResponse(value: Price)

object InstancePriceResponse {

  implicit val encoder: Encoder[InstancePriceResponse] =
    Encoder.instance[InstancePriceResponse] {
      case InstancePriceResponse(k) =>
        Json.obj(
          "price" -> k.price.asJson
        )
    }

}
