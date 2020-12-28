package com.adthena.shopping.basket.acceptance

import com.adthena.shopping.basket.ShoppingBasket
import com.adthena.shopping.basket.model._
import org.scalatest.EitherValues._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ShoppingBasketAcceptanceSpec extends AnyFreeSpec with Matchers {
  private val offers: Set[Offers] =
    Set(Apples10Percent, TinsOfSoupBreadHalfPrice)

  "ShoppingBasket should calculate and show to the console" - {
    "the Subtotal, the Apple's offer and the final price of the basket" in {
      val items = Right(List(Apple, Apple, Bread))
      val validatedOutput = ShoppingBasket.run(items, offers)

      val expectedSubtotal = (2 * Apple.price) + (1 * Bread.price)
      val expectedDiscounts = (2 * Apples10Percent.discountValue)
      val expectedTotalPrice = expectedSubtotal - expectedDiscounts
      validatedOutput.result.value should ===(
        s"""Subtotal: £$expectedSubtotal
           |Apples 10% off: £${expectedDiscounts}
           |Total price: £${expectedTotalPrice}""".stripMargin
      )
    }
    "the Subtotal, both offers and the final price of the basket" in {
      val items = Right(List(Apple, Soup, Soup, Bread))
      val validatedOutput = ShoppingBasket.run(items, offers)

      val expectedSubtotal =
        (1 * Apple.price) + (2 * Soup.price) + (1 * Bread.price)
      val expectedAppleDiscount =
        (1 * Apples10Percent.discountValue)
      val expectedBreadDiscount =
        (1 * TinsOfSoupBreadHalfPrice.discountValue)
      val expectedTotalPrice =
        expectedSubtotal - expectedAppleDiscount - expectedBreadDiscount
      validatedOutput.result.value should ===(
        s"""Subtotal: £$expectedSubtotal
           |Apples 10% off: £${Apples10Percent.discountValue}
           |2 tins of Soup, bread half price off: £${TinsOfSoupBreadHalfPrice.discountValue}
           |Total price: £${expectedTotalPrice}""".stripMargin
      )
    }
    "the Subtotal, no offers and the final price of the basket" in {
      val items = Right(List(Milk, Bread))
      val validatedOutput = ShoppingBasket.run(items, offers)

      val expectedSubtotal = (1 * Milk.price) + (1 * Bread.price)
      val expectedTotalPrice = expectedSubtotal

      validatedOutput.result.value should ===(
        s"""Subtotal: £$expectedSubtotal
           |(No offers available)
           |Total price: £${expectedTotalPrice}""".stripMargin
      )
    }
  }
}
