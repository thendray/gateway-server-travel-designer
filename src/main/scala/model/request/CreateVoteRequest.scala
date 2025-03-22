package model.request

case class CreateVoteRequest(userId: Long, cardId: Long, mark: Int)
