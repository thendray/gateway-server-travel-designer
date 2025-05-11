package model.request

case class CreateVoteRequest(userId: Long, cardId: Long, routeId: Long, mark: Int)
