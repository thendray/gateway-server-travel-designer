package model.request

case class UpdateRouteRequest(
    routeId: Long,
    days: Option[Long],
    name: Option[String],
    login: Option[String],
    password: Option[String],
    cardLimit: Option[Int])
