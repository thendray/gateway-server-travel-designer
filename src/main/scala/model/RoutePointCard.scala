package model

case class RoutePointCard(
    id: Long,
    name: String,
    routePoint: RoutePoint,
    author: String,
    description: String,
    category: String,
    photo: String,
    rating: String)
