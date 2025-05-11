package model.response

import model.response.GetRouteMembersResponse.RouteMemberResponse


case class GetRouteMembersResponse(members: List[RouteMemberResponse], routeCreatorId: Long)

object GetRouteMembersResponse {
  case class RouteMemberResponse(userId: Long, name: String)
}
