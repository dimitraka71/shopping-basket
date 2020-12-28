name := "shopping-basket"

version := "0.1"

scalaVersion := "2.13.4"

val scalaTestVersion = "3.2.3"
val scalaCheckVersion = "1.15.1"
val catsVersion = "2.0.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test,
  "com.github.chocpanda" %% "scalacheck-magnolia" % "0.5.1" % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.1.0" % Test
)

scalacOptions in Test ++= Seq("-Yrangepos")
