package model.response

case class FullRouteResponse(
    id: Long,
    name: String,
    days: Int,
    beginAddress: String,
    endAddress: String,
    login: String,
    password: String,
    cardLimit: Int)
