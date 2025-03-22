package model

case class RouteSpecialSettings(
    id: Long,
    routeId: Long,
    beginPoint: Option[RoutePoint],
    endPoint: Option[RoutePoint])
