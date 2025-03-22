package handler

import clients.{MainClient, UserClient}
import clients.UserClient.UserInfo
import model.User._
import model.{Route, RouteError, RoutePointCard, Vote}
import model.request.{AddPointToRouteRequest, AddToFavoriteRequest, CreateCardRequest, CreateVoteRequest, DeletePointFromRouteRequest, JoinRouteRequest, UpdateCardRequest, UpdatePointInRouteRequest, UpdateRouteRequest}
import model.response.{CardWithVote, FullRouteResponse, GetPointCardsResponse, GetRouteDetailsByDayResponse, GetRouteMembersResponse, GetUserRoutesResponse, SimpleCardResponse, SimpleRouteResponse}
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import util.JwtService
import zio.{IO, Task, ZIO, ZLayer}

class MyRouter(userClient: UserClient, mainClient: MainClient) {

  private def checkJwt(jwt: String): IO[String, Unit] = {
    val token =
      jwt.split(" ").drop(1).headOption.getOrElse(throw new IllegalArgumentException(s"Miss header: $jwt"))
    ZIO.ifZIO(JwtService.isTokenValid(token))(
      ZIO.unit,
      ZIO.fail(throw new IllegalArgumentException(s"Bad header: $jwt"))
    )
  }.mapError(e => e.toString)

  private val createRouteHandler: Route => ZIO[Any, String, SimpleRouteResponse] =
    request => mainClient.createRoute(request).mapError(e => e.toString)

  val createRoute =
    BaseEndpoints.createRouteEndpoint.zServerLogic(createRouteHandler)
//
//  private val updateRouteHandler: ((String, UpdateRouteRequest)) => ZIO[Any, String, SimpleRouteResponse] =
//    request => (checkJwt(request._1) *> mainClient.updateRoute(request._2)).mapError(e => e.toString)
//
//  val updateRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.updateRouteEndpoint.zServerLogic(updateRouteHandler)
//
//  private val joinRouteHandler: ((String, JoinRouteRequest)) => ZIO[Any, String, SimpleRouteResponse] =
//    request => (checkJwt(request._1) *> mainClient.joinRoute(request._2)).mapError(e => e.toString)
//
//  val joinRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.joinRouteEndpoint.zServerLogic(joinRouteHandler)
//
//  private val getRouteCardsHandler: ((String, Long)) => ZIO[Any, String, GetPointCardsResponse] =
//    routeId => (checkJwt(routeId._1) *> mainClient.getRouteCards(routeId._2)).mapError(e => e.toString)
//
//  val getRouteCards: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getRouteCards.zServerLogic(getRouteCardsHandler)
//
//  private val getFavoriteCardsHandler: ((String, Long)) => ZIO[Any, String, GetPointCardsResponse] =
//    userId => (checkJwt(userId._1) *> mainClient.getFavoriteCards(userId._2)).mapError(e => e.toString)
//
//  val getFavoriteCards: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getFavoriteCards.zServerLogic(getFavoriteCardsHandler)
//
//  private val getUserRoutesHandler: ((String, Long)) => ZIO[Any, String, GetUserRoutesResponse] =
//    userId => (checkJwt(userId._1) *> mainClient.getUserRoutes(userId._2)).mapError(e => e.toString)
//
//  val getUserRoutes: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getUserRoutes.zServerLogic(getUserRoutesHandler)
//
//  private val getRouteHandler: ((String, Long)) => ZIO[Any, String, FullRouteResponse] =
//    userId => (checkJwt(userId._1) *> mainClient.getRoute(userId._2)).mapError(e => e.toString)
//
//  val getRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getRoute.zServerLogic(getRouteHandler)
//
//  private val getRouteDetailsByDayHandler: ((String, Long, Int)) => ZIO[Any, String, GetRouteDetailsByDayResponse] =
//    params => (checkJwt(params._1) *> mainClient.getRouteDetailsByDay(params._2, params._3)).mapError(e => e.toString)
//
//  val getRouteDetailsByDay: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getRouteDetailsByDay.zServerLogic(getRouteDetailsByDayHandler)
//
//  private val getRouteMembersHandler: ((String, Long)) => ZIO[Any, String, GetRouteMembersResponse] =
//    routeId => (checkJwt(routeId._1) *> mainClient.getRouteMembers(routeId._2)).mapError(e => e.toString)
//
//  val getRouteMembers: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getRouteMembers.zServerLogic(getRouteMembersHandler)
//
//  private val addPointToRouteHandler: ((String, AddPointToRouteRequest)) => ZIO[Any, String, String] =
//    request => (checkJwt(request._1) *> mainClient.addPointToRoute(request._2)).mapError(e => e.toString)
//
//  val addPointToRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.addPointToRoute.zServerLogic(addPointToRouteHandler)
//
//  private val deletePointFromRouteHandler: ((String, DeletePointFromRouteRequest)) => ZIO[Any, String, String] =
//    request => (checkJwt(request._1) *> mainClient.deletePointFromRoute(request._2)).mapError(e => e.toString)
//
//  val deletePointFromRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.deletePointFromRoute.zServerLogic(deletePointFromRouteHandler)
//
//  private val movePointInRouteHandler: ((String, UpdatePointInRouteRequest)) => ZIO[Any, String, String] =
//    request => (checkJwt(request._1) *> mainClient.movePointInRoute(request._2)).mapError(e => e.toString)
//
//  val movePointInRoute: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.movePointInRoute.zServerLogic(movePointInRouteHandler)
//
//  private val createCardHandler: ((String, CreateCardRequest)) => ZIO[Any, String, SimpleCardResponse] =
//    request => (checkJwt(request._1) *> mainClient.createCard(request._2)).mapError(e => e.toString)
//
//  val createCard: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.createCard.zServerLogic(createCardHandler)
//
//  private val updateCardHandler: ((String, UpdateCardRequest)) => ZIO[Any, String, SimpleCardResponse] =
//    request => (checkJwt(request._1) *> mainClient.updateCard(request._2)).mapError(e => e.toString)
//
//  val updateCard: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.updateCard.zServerLogic(updateCardHandler)
//
//  private val getCardHandler: ((String, Long)) => ZIO[Any, String, CreateCardRequest] =
//    id => (checkJwt(id._1) *> mainClient.getCard(id._2)).mapError(e => e.toString)
//
//  val getCard: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getCard.zServerLogic(getCardHandler)
//
//  private val deleteCardHandler: ((String, Long)) => ZIO[Any, String, String] =
//    id => (checkJwt(id._1) *> mainClient.deleteCard(id._2)).mapError(e => e.toString)
//
//  val deleteCard: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.deleteCard.zServerLogic(deleteCardHandler)
//
//  private val getUserRouteCardsHandler: ((String, Long, Long)) => ZIO[Any, String, GetPointCardsResponse] =
//    ids => (checkJwt(ids._1) *> mainClient.getUserRouteCards(ids._2, ids._3)).mapError(e => e.toString)
//
//  val getUserRouteCards: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getUserRouteCards.zServerLogic(getUserRouteCardsHandler)
//
//  private val addToFavoriteHandler: ((String, AddToFavoriteRequest)) => ZIO[Any, String, String] =
//    request => checkJwt(request._1) *> mainClient.addToFavorite(request._2).mapError(e => e.toString)
//
//  val addToFavorite: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.addToFavorite.zServerLogic(addToFavoriteHandler)
//
//  // ----
//
//  private val createVoteHandler: ((String, CreateVoteRequest)) => ZIO[Any, String, String] =
//    request => checkJwt(request._1) *> mainClient.createVote(request._2).mapError(e => e.toString)
//
//  val createVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.createVote.zServerLogic(createVoteHandler)
//
//  private val deleteVoteHandler: ((String, Long, Long)) => ZIO[Any, String, String] =
//    ids => checkJwt(ids._1) *> mainClient.deleteVote(ids._2, ids._3).mapError(e => e.toString)
//
//  val deleteVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.deleteVote.zServerLogic(deleteVoteHandler)
//
//  private val getVoteHandler: ((String, Long, Long)) => ZIO[Any, String, Vote] =
//    ids => checkJwt(ids._1) *> mainClient.getVote(ids._2, ids._3).mapError(e => e.toString)
//
//  val getVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getVote.zServerLogic(getVoteHandler)
//
//  private val updateVoteHandler: ((String, CreateVoteRequest)) => ZIO[Any, String, String] =
//    request => checkJwt(request._1) *> mainClient.updateVote(request._2).mapError(e => e.toString)
//
//  val updateVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.updateVote.zServerLogic(updateVoteHandler)
//
//  private val getCardsWithUserVoteHandler: ((String, Long, Long)) => ZIO[Any, String, List[CardWithVote]] =
//    ids => checkJwt(ids._1) *> mainClient.getCardsWithUserVote(ids._2, ids._3).mapError(e => e.toString)
//
//  val getCardsWithUserVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getCardsWithUserVote.zServerLogic(getCardsWithUserVoteHandler)
//
//  private val getCardsForVoteHandler: ((String, Long, Long)) => ZIO[Any, String, List[RoutePointCard]] =
//    ids => checkJwt(ids._1) *> mainClient.getCardsForVote(ids._3, ids._2).mapError(e => e.toString)
//
//  val getCardsForVote: ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getCardsForVote.zServerLogic(getCardsForVoteHandler)
//
//  private val registerHandler: RegisterRequest => ZIO[Any, String, AuthResponse] =
//    request => userClient.register(request).mapError(e => e.toString)
//
//  private val loginHandler: LoginRequest => ZIO[Any, String, AuthResponse] =
//    request => userClient.login(request).mapError(e => e.toString)
//
//  private val getUserHandler: ((String, Long)) => ZIO[Any, String, UserInfo] =
//    userId => checkJwt(userId._1) *> userClient.getUserById(userId._2).mapError(e => e.toString)
//
//  private val updateUserHandler: ((String, UserUpdateRequest)) => ZIO[Any, String, Unit] =
//    request => checkJwt(request._1) *> userClient.updateUser(request._2).mapError(e => e.toString)
//
//  private val deleteUserHandler: ((String, Int)) => ZIO[Any, String, Unit] =
//    userId => checkJwt(userId._1) *> userClient.deleteUser(userId._2).mapError(e => e.toString)
//
//  def reg(): ZServerEndpoint[Any, Any] =
//    BaseEndpoints.registerEndpoint.zServerLogic(registerHandler)
//
//  def log(): ZServerEndpoint[Any, Any] =
//    BaseEndpoints.loginEndpoint.zServerLogic(loginHandler)
//
//  def get(): ZServerEndpoint[Any, Any] =
//    BaseEndpoints.getUserEndpoint.zServerLogic(getUserHandler)
//
//  def update(): ZServerEndpoint[Any, Any] =
//    BaseEndpoints.updateUserEndpoint.zServerLogic(updateUserHandler)
//
//  def delete(): ZServerEndpoint[Any, Any] =
//    BaseEndpoints.deleteUserEndpoint.zServerLogic(deleteUserHandler)
}

object MyRouter {

  val live: ZLayer[UserClient with MainClient, Nothing, MyRouter] =
    ZLayer.fromFunction((c: UserClient, m: MainClient) => new MyRouter(c, m))
}
