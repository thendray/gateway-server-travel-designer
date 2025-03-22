package model.request

case class UpdatePointInRouteRequest(routeId: Long, cardId: Long, day: Int, currentPosition: Int, newPosition: Int)
