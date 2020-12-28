package com.adthena.shopping.basket

import cats.implicits._
import com.adthena.shopping.basket.model._

import scala.io.StdIn.readLine

object InputReader {

  def read: Either[Throwable, List[Goods]] = {
    println(
      "Please insert goods you would like to buy in the format: PriceBasket item1 item2 item3 ..."
    )

    readLine()
      .split(" ")
      .toList
      .filterNot(_ == "PriceBasket")
      .traverse {
        case "Apple" => Right(Apple)
        case "Milk"  => Right(Milk)
        case "Bread" => Right(Bread)
        case "Soup"  => Right(Soup)
        case other =>
          Left(
            throw new Exception(
              s"Received goody='$other' which is not supported, please choose one of Apple, Milk, Bread, Soup"
            )
          )
      }
  }
}
