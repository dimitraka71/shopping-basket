package com.adthena.shopping.basket

import com.adthena.shopping.basket.model.{ValidatedOutput, _}
import com.adthena.shopping.basket.services.BasketCalculation
import cats.implicits._

object ShoppingBasket {

  def run(
      goodsFromInput: Either[Throwable, List[Goods]],
      offers: Set[Offers]
  ): ValidatedOutput = {
    val basketCalculationService = BasketCalculation()

    ValidatedOutput(
      goodsFromInput
        .map { goods =>
          val subtotalLine = PriceOutput(
            "Subtotal",
            basketCalculationService.calculateSubtotal(goods)
          ).show

          val totalDiscountsLine =
            OffersOutput(
              basketCalculationService.calculateTotalDiscount(goods, offers)
            ).show

          val totalPriceLine =
            PriceOutput(
              "Total price",
              basketCalculationService.calculateTotalPrice(goods, offers)
            ).show

          s"""$subtotalLine
           |$totalDiscountsLine
           |$totalPriceLine""".stripMargin
        }
    )
  }
}
