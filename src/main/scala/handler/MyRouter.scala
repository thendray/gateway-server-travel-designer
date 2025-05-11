package handler

import clients.RecommendationClient.PlaceRecommendationResponse
import clients.{MainClient, RecommendationClient, UserClient}
import clients.UserClient.UserInfo
import model.User._
import model.{Route, RouteError, RoutePointCard, Vote}
import model.request.{AddPointToRouteRequest, AddToFavoriteRequest, CopyRequest, CreateCardRequest, CreateVoteRequest, DeletePointFromRouteRequest, ExtraSettings, JoinRouteRequest, UpdateCardRequest, UpdatePointInRouteRequest, UpdateRouteRequest}
import model.response.GetRouteMembersResponse.RouteMemberResponse
import model.response.{FullRouteResponse, GetPointCardsResponse, GetRouteDetailsByDayResponse, GetRouteMembersResponse, GetUserRoutesResponse, GetuserPointCardsResponse, RouteRecommendationResponse, SimpleCardResponse, SimpleRouteResponse, VotesForCard}
import sttp.tapir.ztapir._
import util.JwtService
import zio.{IO, Task, ZIO, ZLayer}

class MyRouter(userClient: UserClient, mainClient: MainClient, recommendationClient: RecommendationClient) {

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

  val createRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.createRouteEndpoint.serverLogic(_ => createRouteHandler)

  private val addSettingsHandler: ExtraSettings => ZIO[Any, String, String] =
    request => mainClient.addSettings(request).mapError(e => e.toString)

  val addSettings: ZServerEndpoint[Any, Any] =
    BaseEndpoints.addSettingsEndpoint.serverLogic(_ => addSettingsHandler)

  private val updateRouteHandler: UpdateRouteRequest => ZIO[Any, String, SimpleRouteResponse] =
    request => mainClient.updateRoute(request).mapError(e => e.toString)

  val updateRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.updateRouteEndpoint.serverLogic(_ => updateRouteHandler)

  private val joinRouteHandler: JoinRouteRequest => ZIO[Any, String, SimpleRouteResponse] =
    request => mainClient.joinRoute(request).mapError(e => e.toString)

  val joinRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.joinRouteEndpoint.serverLogic(_ => joinRouteHandler)

//
  private val getRouteCardsHandler: Long => ZIO[Any, String, GetPointCardsResponse] =
    routeId => mainClient.getRouteCards(routeId).mapError(e => e.toString)

  val getRouteCards: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRouteCards.serverLogic(_ => getRouteCardsHandler)

  private val getFavoriteCardsHandler: Long => ZIO[Any, String, GetPointCardsResponse] =
    userId => mainClient.getFavoriteCards(userId).mapError(e => e.toString)

  val getFavoriteCards: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getFavoriteCards.serverLogic(_ => getFavoriteCardsHandler)

  private val getUserRoutesHandler: Long => ZIO[Any, String, GetUserRoutesResponse] =
    userId => mainClient.getUserRoutes(userId).mapError(e => e.toString)

  val getUserRoutes: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getUserRoutes.serverLogic(_ => getUserRoutesHandler)

  private val getRouteHandler: Long => ZIO[Any, String, FullRouteResponse] =
    userId => mainClient.getRoute(userId).mapError(e => e.toString)

  val getRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRoute.serverLogic(_ => getRouteHandler)

  private val getRouteDetailsByDayHandler: ((Long, Int)) => ZIO[Any, String, GetRouteDetailsByDayResponse] =
    params => mainClient.getRouteDetailsByDay(params._1, params._2).mapError(e => e.toString)

  val getRouteDetailsByDay: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRouteDetailsByDay.serverLogic(_ => getRouteDetailsByDayHandler)

  private val getRouteMembersHandler: Long => ZIO[Any, String, GetRouteMembersResponse] =
    routeId => mainClient.getRouteMembers(routeId).mapError(e => e.toString)

  val getRouteMembers: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRouteMembers.serverLogic(_ => getRouteMembersHandler)

  private val deleteRouteMemberHandler: ((Long, Long)) => ZIO[Any, String, String] =
    ids => mainClient.deleteRouteMember(ids._1, ids._2).mapError(e => e.toString)

  val deleteRouteMember: ZServerEndpoint[Any, Any] =
    BaseEndpoints.deleteRouteMember.serverLogic(_ => deleteRouteMemberHandler)

  private val addRouteMemberHandler: ((Long, Long)) => ZIO[Any, String, RouteMemberResponse] =
    ids => mainClient.addRouteMember(ids._1, ids._2).mapError(e => e.toString)

  val addRouteMember: ZServerEndpoint[Any, Any] =
    BaseEndpoints.addRouteMember.serverLogic(_ => addRouteMemberHandler)

  private val addPointToRouteHandler: AddPointToRouteRequest => ZIO[Any, String, String] =
    request => mainClient.addPointToRoute(request).mapError(e => e.toString)

  val addPointToRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.addPointToRoute.serverLogic(_ => addPointToRouteHandler)

  private val deletePointFromRouteHandler: DeletePointFromRouteRequest => ZIO[Any, String, String] =
    request => mainClient.deletePointFromRoute(request).mapError(e => e.toString)

  val deletePointFromRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.deletePointFromRoute.serverLogic(_ => deletePointFromRouteHandler)

  private val movePointInRouteHandler: UpdatePointInRouteRequest => ZIO[Any, String, String] =
    request => mainClient.movePointInRoute(request).mapError(e => e.toString)

  val movePointInRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.movePointInRoute.serverLogic(_ => movePointInRouteHandler)

  private val createCardHandler: CreateCardRequest => ZIO[Any, String, SimpleCardResponse] =
    request => mainClient.createCard(request).mapError(e => e.toString)

  val createCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.createCard.serverLogic(_ => createCardHandler)

  private val copyCardHandler: CreateCardRequest => ZIO[Any, String, SimpleCardResponse] =
    request => mainClient.copyCard(request).mapError(e => e.toString)

  val copyCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.copyCard.serverLogic(_ => copyCardHandler)

  private val updateCardHandler: UpdateCardRequest => ZIO[Any, String, SimpleCardResponse] =
    request => mainClient.updateCard(request).mapError(e => e.toString)

  val updateCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.updateCard.serverLogic(_ => updateCardHandler)

  private val getCardHandler: Long => ZIO[Any, String, CreateCardRequest] =
    id => mainClient.getCard(id).mapError(e => e.toString)

  val getCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getCard.serverLogic(_ => getCardHandler)

  private val deleteCardHandler: Long => ZIO[Any, String, String] =
    id => mainClient.deleteCard(id).mapError(e => e.toString)

  val deleteCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.deleteCard.serverLogic(_ => deleteCardHandler)

  private val getUserRouteCardsHandler: ((Long, Long)) => ZIO[Any, String, GetuserPointCardsResponse] =
    ids => mainClient.getUserRouteCards(ids._1, ids._2).mapError(e => e.toString)

  val getUserRouteCards: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getUserRouteCards.serverLogic(_ => getUserRouteCardsHandler)

  private val copyRouteHandler: CopyRequest => ZIO[Any, String, String] =
    request => mainClient.copyRoute(request).mapError(e => e.toString)

  val copyRoute: ZServerEndpoint[Any, Any] =
    BaseEndpoints.copyRoute.serverLogic(_ => copyRouteHandler)

  private val addToFavoriteHandler: AddToFavoriteRequest => ZIO[Any, String, String] =
    request => mainClient.addToFavorite(request).mapError(e => e.toString)

  val addToFavorite: ZServerEndpoint[Any, Any] =
    BaseEndpoints.addToFavorite.serverLogic(_ => addToFavoriteHandler)

  private val deleteFavoriteHandler: AddToFavoriteRequest => ZIO[Any, String, String] =
    request => mainClient.deleteFavorite(request).mapError(e => e.toString)

  val deleteFavorite: ZServerEndpoint[Any, Any] =
    BaseEndpoints.deleteFavorite.serverLogic(_ => deleteFavoriteHandler)

  // ----

  private val createVoteHandler: CreateVoteRequest => ZIO[Any, String, String] =
    request => mainClient.createVote(request).mapError(e => e.toString)

  val createVote: ZServerEndpoint[Any, Any] =
    BaseEndpoints.createVote.serverLogic(_ => createVoteHandler)

  private val deleteVoteHandler: ((Long, Long)) => ZIO[Any, String, String] =
    ids => mainClient.deleteVote(ids._1, ids._2).mapError(e => e.toString)

  val deleteVote: ZServerEndpoint[Any, Any] =
    BaseEndpoints.deleteVote.serverLogic(_ => deleteVoteHandler)

  private val getVoteHandler: ((Long, Long)) => ZIO[Any, String, Vote] =
    ids => mainClient.getVote(ids._1, ids._2).mapError(e => e.toString)

  val getVote: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getVote.serverLogic(_ => getVoteHandler)

  private val updateVoteHandler: CreateVoteRequest => ZIO[Any, String, String] =
    request => mainClient.updateVote(request).mapError(e => e.toString)

  val updateVote: ZServerEndpoint[Any, Any] =
    BaseEndpoints.updateVote.serverLogic(_ => updateVoteHandler)

  private val getVotesForCardHandler: ((Long, Long)) => ZIO[Any, String, VotesForCard] =
    ids => mainClient.getVotesForCard(ids._1, ids._2).mapError(e => e.toString)

  val getVotesForCard: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getVotesForCard.serverLogic(_ => getVotesForCardHandler)

  private val getCardsForVoteHandler: ((Long, Long)) => ZIO[Any, String, List[RoutePointCard]] =
    ids => mainClient.getCardsForVote(ids._1, ids._2).mapError(e => e.toString)

  val getCardsForVote: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getCardsForVote.serverLogic(_ => getCardsForVoteHandler)

  private val registerHandler: RegisterRequest => ZIO[Any, String, AuthResponse] =
    request => userClient.register(request).mapError(e => e.toString)

  private val loginHandler: LoginRequest => ZIO[Any, String, AuthResponse] =
    request => userClient.login(request).mapError(e => e.toString)

  private val getUserHandler: Long => ZIO[Any, String, UserInfo] =
    userId => userClient.getUserById(userId).mapError(e => e.toString)

  private val updateUserHandler: UserUpdateRequest => ZIO[Any, String, Unit] =
    request => userClient.updateUser(request).mapError(e => e.toString)

  private val deleteUserHandler: Int => ZIO[Any, String, Unit] =
    userId => userClient.deleteUser(userId).mapError(e => e.toString)

  def reg(): ZServerEndpoint[Any, Any] =
    BaseEndpoints.registerEndpoint.zServerLogic(registerHandler)

  def log(): ZServerEndpoint[Any, Any] =
    BaseEndpoints.loginEndpoint.zServerLogic(loginHandler)

  def get(): ZServerEndpoint[Any, Any] =
    BaseEndpoints.getUserEndpoint.serverLogic(_ => getUserHandler)

  def update(): ZServerEndpoint[Any, Any] =
    BaseEndpoints.updateUserEndpoint.serverLogic(_ => updateUserHandler)

  def delete(): ZServerEndpoint[Any, Any] =
    BaseEndpoints.deleteUserEndpoint.serverLogic(_ => deleteUserHandler)

  // -----

  private val getRecPlacesHandler: Long => ZIO[Any, String, PlaceRecommendationResponse] =
    userId => recommendationClient.getPlaces(userId).mapError(e => e.toString)

  val getRecPlaces: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRecPlacesEndpoint.serverLogic(_ => getRecPlacesHandler)

  private val getRecRoutesHandler: Long => ZIO[Any, String, RouteRecommendationResponse] =
    userId => recommendationClient.getRoutes(userId).mapError(e => e.toString)

  val getRecRoutes: ZServerEndpoint[Any, Any] =
    BaseEndpoints.getRecRoutesEndpoint.serverLogic(_ => getRecRoutesHandler)
}

object MyRouter {

  val live: ZLayer[UserClient with MainClient with RecommendationClient, Nothing, MyRouter] =
    ZLayer.fromFunction((c: UserClient, m: MainClient, r: RecommendationClient) => new MyRouter(c, m, r))
}
