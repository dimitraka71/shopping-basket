package com.adthena.shopping.basket

import com.adthena.shopping.basket.model._
import com.adthena.shopping.basket.services.BasketCalculation
import org.scalacheck.{Arbitrary, Gen, magnolia}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class BasketCalculationSpec
    extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks {

  private val offers: Set[Offers] =
    Set(Apples10Percent, TinsOfSoupBreadHalfPrice)
  private val basketCalculation = BasketCalculation()

  "BasketCalculation" - {
    implicit val goodsArb: Arbitrary[Goods] = magnolia.gen[Goods]
    "calculateSubtotal should" - {
      "not throw any exception when calculating the subtotal amount" in forAll {
        (items: List[Goods]) =>
          val result = basketCalculation.calculateSubtotal(items)
          result shouldBe (a[BigDecimal])
      }
      "calculate correctly the aggregated amount for all the items in the basket" in {
        val items = List(Apple, Apple, Milk, Apple, Bread)
        val result = basketCalculation.calculateSubtotal(items)
        result should ===((3 * Apple.price) + Milk.price + Bread.price)
      }
    }

    "calculateDiscounts should" - {
      def applesListGen: Gen[List[Apple.type]] =
        for {
          totalItems <- Gen.choose(1, 100)
          goodsGen <- magnolia.gen[Apple.type].arbitrary
          list <- Gen.listOfN(totalItems, goodsGen)
        } yield list

      "calculate the 10% offer on Apples only" in forAll(applesListGen) {
        items =>
          val result =
            basketCalculation.calculateTotalDiscount(Milk +: items, offers)
          val expectedDiscount = items.size * Apples10Percent.discountValue

          result should ===(
            Set(
              (Apples10Percent.name, expectedDiscount)
            )
          )
      }
      "return empty Set when basket is empty" in {
        val result =
          basketCalculation.calculateTotalDiscount(Nil, offers)

        result should ===(Set.empty)
      }
      "return empty Set when there are no offers" in { (items: List[Goods]) =>
        val result =
          basketCalculation.calculateTotalDiscount(items, Set.empty)

        result should ===(Set.empty)
      }
      "return empty Set when there are at least 2 Soups but not bread in the basket" in {
        val items = List(Soup, Milk, Soup, Soup)
        val result = basketCalculation.calculateTotalDiscount(items, offers)
        result should ===(Set.empty)
      }
      "return empty Set when there is 1 Soup and 1 Bread" in {
        val items = List(Soup, Milk, Bread)
        val result = basketCalculation.calculateTotalDiscount(items, offers)
        result should ===(Set.empty)
      }

      "apply the discount for 5 breads when tins of Soup are 10" in {
        val tinsOfSoup = List.fill(10)(Soup)
        val breads = List.fill(5)(Bread)
        val result = basketCalculation
          .calculateTotalDiscount(tinsOfSoup ::: breads, offers)

        result should ===(
          Set(
            (
              TinsOfSoupBreadHalfPrice.name,
              breads.size * TinsOfSoupBreadHalfPrice.discountValue
            )
          )
        )
      }

      def soupAndBreadDistributionGen
          : Gen[(List[Soup.type], List[Bread.type])] =
        for {
          numberOfTinsOfSoup <- Gen.choose(2, 1000)
          soupGen <- magnolia.gen[Soup.type].arbitrary
          breadGen <- magnolia.gen[Bread.type].arbitrary
          listOfTinsOfSoup <- Gen.listOfN(numberOfTinsOfSoup, soupGen)
          listOfBreads <- Gen.listOfN(numberOfTinsOfSoup / 2, breadGen)
        } yield (listOfTinsOfSoup, listOfBreads)
      "apply the discount for all the breads contained in the basket when ratio is 1 Bread / 2 tins of Soup" in forAll(
        soupAndBreadDistributionGen
      ) {
        case (tinsOfSoup, breads) =>
          val result = basketCalculation
            .calculateTotalDiscount(tinsOfSoup ::: breads, offers)

          result should ===(
            Set(
              (
                TinsOfSoupBreadHalfPrice.name,
                breads.size * TinsOfSoupBreadHalfPrice.discountValue
              )
            )
          )
      }

      def soupAndBreadDistributionGen2
          : Gen[(List[Soup.type], List[Bread.type])] =
        for {
          soupGen <- magnolia.gen[Soup.type].arbitrary
          breadGen <- magnolia.gen[Bread.type].arbitrary
          listOfTinsOfSoup <- Gen.listOfN(1000, soupGen)
          listOfBreads <- Gen.listOfN(800, breadGen)
        } yield (listOfTinsOfSoup, listOfBreads)
      "apply the discount for 500 breads out of 800 when tins of Soup are 1000 " in forAll(
        soupAndBreadDistributionGen2
      ) {
        case (tinsOfSoup, breads) =>
          val result = basketCalculation
            .calculateTotalDiscount(tinsOfSoup ::: breads, offers)

          result should ===(
            Set(
              (
                TinsOfSoupBreadHalfPrice.name,
                500 * TinsOfSoupBreadHalfPrice.discountValue
              )
            )
          )
      }
    }

    "calculateTotalPrice should" - {
      "not throw any exception when calculating the total price of the basket" in forAll {
        (items: List[Goods]) =>
          val result = basketCalculation.calculateTotalPrice(items, offers)
          result shouldBe (a[BigDecimal])
      }

      "equal the subtotal amount when no offer is applicable" in {
        val items = List(Milk, Bread, Bread, Soup)
        val totalPrice = basketCalculation.calculateTotalPrice(items, offers)
        val subtotal = basketCalculation.calculateSubtotal(items)

        totalPrice should ===(
          (1 * Milk.price) + (2 * Bread.price) + (1 * Soup.price)
        )
        totalPrice should ===(subtotal)
      }

      "subtract the discount from the subtotal amount" in {
        val items = List(Apple, Soup, Soup, Bread)
        val result = basketCalculation.calculateTotalPrice(items, offers)

        result should ===(
          (1 * (Apple.price - Apples10Percent.discountValue)) + (2 * Soup.price) + (1 * TinsOfSoupBreadHalfPrice.discountValue)
        )
      }
    }
  }

}
