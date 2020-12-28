package com.adthena.shopping.basket.model

sealed trait Offers {
  type OfferName = String
  def name: OfferName
  def discountValue: BigDecimal

  /** Calculates the discount for an offer */
  def calculateDiscount(items: List[Goods]): BigDecimal
}

case object Apples10Percent extends Offers {
  override def name: OfferName = "Apples 10% off"
  override def discountValue: BigDecimal = Apple.price * 0.1
  override def calculateDiscount(items: List[Goods]): BigDecimal =
    items.filter(_ == Apple).map(_ => discountValue).sum
}

case object TinsOfSoupBreadHalfPrice extends Offers {
  override def name: OfferName = "2 tins of Soup, bread half price off"
  override def discountValue: BigDecimal = Bread.price / 2

  /** Creates groups of 2 tins of Soup zipped with Breads,
    * e.g. List((Bread,List(Soup, Soup)), (Bread,List(Soup, Soup), ... )
    * applying the discount on each group
    */
  override def calculateDiscount(items: List[Goods]): BigDecimal = {
    val tinsOfSoupGroupsOfTwo =
      items
        .filter(_ == Soup)
        .grouped(2)
        .toList
        .takeWhile(_.size == 2)
    val breads = items.filter(_ == Bread)

    breads
      .zip(tinsOfSoupGroupsOfTwo)
      .map(_ => discountValue)
      .sum
  }
}
