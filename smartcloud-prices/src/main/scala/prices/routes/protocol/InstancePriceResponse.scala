package prices.routes.protocol

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import prices.services.Price

final case class InstancePriceResponse(value: Price, kind: String)

object InstancePriceResponse {

  implicit val encoder: Encoder[InstancePriceResponse] =
    Encoder.instance[InstancePriceResponse] {
      case InstancePriceResponse(price, kind) =>
        Json.obj(
          "kind" -> kind.asJson,
          "amount" -> price.price.asJson
        )
    }

}
