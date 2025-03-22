package model.response

import model.response.GetRouteDetailsByDayResponse.PointInfo

case class GetRouteDetailsByDayResponse(routeId: Long, day: Int, pointNamesInOrder: List[PointInfo])

object GetRouteDetailsByDayResponse {
  case class PointInfo(cardId: Long, name: String)
}
