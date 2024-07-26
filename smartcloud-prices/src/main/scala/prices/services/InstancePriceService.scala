package prices.services

import io.circe.Decoder


trait InstancePriceService[F[_]] {
  def getPrice(kind: String): F[Price]
}

final case class Price(
                        price: Double
                      )

object Price {
  implicit val priceDecoder: Decoder[Price] = Decoder.forProduct1("price")(Price.apply)
}