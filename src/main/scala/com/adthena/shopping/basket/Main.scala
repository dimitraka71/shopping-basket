package com.adthena.shopping.basket

import cats.implicits._
import com.adthena.shopping.basket.model._

object Main extends App {
  val goodsFromInput: Either[Throwable, List[Goods]] = InputReader.read
  val specialOffers: Set[Offers] =
    Set(Apples10Percent, TinsOfSoupBreadHalfPrice)
  val result = ShoppingBasket.run(goodsFromInput, specialOffers).show
  println(s"$result")
}
