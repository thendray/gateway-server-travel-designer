package model.request

case class DeletePointFromRouteRequest(routeId: Long, cardId: Long, day: Int, currentPosition: Int)
