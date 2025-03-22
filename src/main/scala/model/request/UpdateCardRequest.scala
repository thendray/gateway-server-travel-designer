package model.request

case class UpdateCardRequest(
    authorId: Long,
    routeId: Long,
    name: Option[String],
    xCoord: Option[Double],
    yCoord: Option[Double],
    description: Option[String],
    photo: Option[String],
    address: Option[String],
    category: Option[String])
