package model.response

import model.response.GetUserRoutesResponse.RouteResponse


case class GetUserRoutesResponse(routes: List[RouteResponse])

object GetUserRoutesResponse {

  case class RouteResponse(
      id: Long,
      name: String,
      photos: List[String],
      days: Int,
      beginAddress: String,
      endAddress: String,
      topPlaces: List[TopPlace])

  case class TopPlace(name: String, address: String)
}
