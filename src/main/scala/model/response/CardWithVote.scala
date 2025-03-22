package model.response

import model.{RoutePointCard, Vote}

case class CardWithVote(card: RoutePointCard, vote: Option[Vote])
