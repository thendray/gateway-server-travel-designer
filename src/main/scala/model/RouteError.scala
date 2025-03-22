package model

sealed trait RouteError extends Exception

object RouteError {
  case class DbError(problem: String) extends RouteError
  case class LogicError(message: String) extends RouteError
}
