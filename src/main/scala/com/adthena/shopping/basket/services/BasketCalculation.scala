package com.adthena.shopping.basket.services

import com.adthena.shopping.basket.model.Apples10Percent.OfferName
import com.adthena.shopping.basket.model.{Goods, Offers}

trait BasketCalculation[T <: Goods] {
  type AccumulatedDiscounts = Set[(OfferName, BigDecimal)]

  /** Calculates the overall prices of the items in the basket */
  def calculateSubtotal(items: List[T]): BigDecimal

  /** Calculates the overall discounts per offer
    * e.g. Set(("tins of Soup, bread half price off", 0.20))
    * This function filters the discounts which are non negative
    */
  def calculateTotalDiscount(
      items: List[T],
      offers: Set[Offers]
  ): AccumulatedDiscounts

  /** Calculates the final price of the basket */
  def calculateTotalPrice(items: List[T], offers: Set[Offers]): BigDecimal
}

object BasketCalculation {
  def apply(): BasketCalculation[Goods] =
    new BasketCalculation[Goods] {
      override def calculateSubtotal(items: List[Goods]): BigDecimal =
        items.map(_.price).sum

      override def calculateTotalDiscount(
          items: List[Goods],
          offers: Set[Offers]
      ): AccumulatedDiscounts =
        offers
          .map(offer => (offer.name, offer.calculateDiscount(items)))
          .filter(_._2 > 0)

      override def calculateTotalPrice(
          items: List[Goods],
          offers: Set[Offers]
      ): BigDecimal = {
        val discounts = calculateTotalDiscount(items, offers).map(_._2).sum
        calculateSubtotal(items) - discounts
      }
    }
}
