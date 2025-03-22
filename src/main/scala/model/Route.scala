package model

case class Route(
    id: Long,
    creatorId: Long,
    name: String,
    login: String,
    password: String,
    cardLimit: Option[Int],
    days: Int)
