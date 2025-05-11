package model.response

import model.RoutePointCard
import model.request.ExtraSettings

case class GetRouteDetailsByDayResponse(
    routeId: Long,
    day: Int,
    pointNamesInOrder: List[RoutePointCard],
    extraPoint: Option[ExtraSettings])

object GetRouteDetailsByDayResponse {
  case class PointInfo(cardId: Long, name: String)
}
