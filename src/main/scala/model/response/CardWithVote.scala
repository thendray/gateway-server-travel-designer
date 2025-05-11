package model.response

case class VotesForCard(votes: List[VoteInfo])

case class VoteInfo(userName: String, mark: Int)
