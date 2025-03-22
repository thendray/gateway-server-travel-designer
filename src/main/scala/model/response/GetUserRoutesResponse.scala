package model.response

import model.response.GetUserRoutesResponse.RouteResponse


case class GetUserRoutesResponse(routes: List[RouteResponse])

object GetUserRoutesResponse {

  case class RouteResponse(
      id: Long,
      name: String,
      protos: List[String],
      days: Int,
      beginAddress: String,
      endAddress: String,
      topPlacesName: List[String])
}
