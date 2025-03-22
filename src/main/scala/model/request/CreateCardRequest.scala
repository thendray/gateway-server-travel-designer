package model.request

case class CreateCardRequest(
    authorId: Long,
    routeId: Long,
    name: String,
    xCoord: Double,
    yCoord: Double,
    description: String,
    photo: String,
    address: String,
    category: String)
