package com.adthena.shopping.basket.model

import cats.Show
import com.adthena.shopping.basket.model.Apples10Percent.OfferName

final case class PriceOutput(name: String, amount: BigDecimal)

object PriceOutput {
  implicit val priceOutputShow: Show[PriceOutput] =
    Show.show(x => s"${x.name}: £${x.amount}")
}

final case class OffersOutput(discounts: Set[(OfferName, BigDecimal)])
object OffersOutput {
  implicit val offersOutputShow: Show[OffersOutput] = Show.show { offer =>
    if (offer.discounts.isEmpty) "(No offers available)"
    else offer.discounts.map(x => s"${x._1}: £${x._2}").reduce(_ + "\n" + _)
  }
}

final case class ValidatedOutput(result: Either[Throwable, String])
object ValidatedOutput {
  implicit val resultShow: Show[ValidatedOutput] = Show.show {
    _.result match {
      case Right(value) => value
      case Left(error)  => s"Unexpected error occurred, $error"
    }
  }
}
