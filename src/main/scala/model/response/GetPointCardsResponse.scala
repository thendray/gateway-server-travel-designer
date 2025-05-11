package model.response

import model.RoutePointCard

case class GetPointCardsResponse(cards: List[RoutePointCard])
case class GetuserPointCardsResponse(cards: List[RoutePointCard], cardLimit: Option[Int])