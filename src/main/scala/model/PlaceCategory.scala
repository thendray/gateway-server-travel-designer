package model

sealed trait PlaceCategory

object PlaceCategory {
  case object Food extends PlaceCategory
  case object Bed extends PlaceCategory
  case object Entertainment extends PlaceCategory
  case object Something extends PlaceCategory

  def makeString(category: PlaceCategory): String =
    category match {
      case Food => "food"
      case Bed => "bed"
      case Entertainment => "entertainment"
      case Something => "question"
    }

  def fromString(str: String): PlaceCategory =
    str match {
      case "food" => Food
      case "bed" => Bed
      case "entertainment" => Entertainment
      case "question" => Something
      case category => throw new IllegalArgumentException(s"Unknown category $category")

    }
}

