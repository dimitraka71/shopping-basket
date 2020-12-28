package com.adthena.shopping.basket.model

sealed trait Goods {
  def price: BigDecimal
}

case object Apple extends Goods {
  override def price: BigDecimal = BigDecimal(1)
}

case object Milk extends Goods {
  override def price: BigDecimal = BigDecimal(1.3)
}

case object Bread extends Goods {
  override def price: BigDecimal = BigDecimal(0.80)
}

case object Soup extends Goods {
  override def price: BigDecimal = BigDecimal(0.65)
}
