package handler

import clients.RecommendationClient.PlaceRecommendationResponse
import model.response.GetRouteMembersResponse.RouteMemberResponse
import clients.UserClient.{UserInfo, encoder}
import util.JwtService
import model._
import model.User._
import model.request._
import model.response._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import zio._
import sttp.tapir.ztapir.{RichZEndpoint, auth => zAuth, endpoint => zEndpoint}

import java.lang

object BaseEndpoints {

  private def checkJwt(jwt: String) = {
    println(s"Токен: $jwt")
    val token =
      jwt.split(" ").headOption.getOrElse(throw new IllegalArgumentException(s"Miss header: $jwt"))
    ZIO.ifZIO(JwtService.isTokenValid(token))(
      ZIO.unit,
      ZIO.fail(throw new IllegalArgumentException(s"Bad header: $jwt"))
    )
  }.orElseFail()

  val securedEndpoint = zEndpoint
    .securityIn(auth.bearer[String]())
    .zServerSecurityLogic(checkJwt)
    .mapErrorOut(e => e.toString)(e => throw new IllegalArgumentException(e))

  private val baseRouteEndpoint = securedEndpoint.in("api" / "route")

  private val baseCardEndpoint = securedEndpoint.in("api" / "card")

  private val baseVoteEndpoint = securedEndpoint.in("api" / "vote")

  private val baseUserEndpoint = securedEndpoint.in("api" / "users")

  private val baseRecommendationEndpoint = securedEndpoint.in("api" / "recommendation")

  val createRouteEndpoint =
    baseRouteEndpoint
      .in("create")
      .post
      .in(jsonBody[Route])
      .out(jsonBody[SimpleRouteResponse])
//      .errorOut(jsonBody[String])

  val addSettingsEndpoint =
    baseRouteEndpoint
      .in("add" / "settings")
      .post
      .in(jsonBody[ExtraSettings])
      .out(jsonBody[String])

  val updateRouteEndpoint =
    baseRouteEndpoint
      .in("update")
      .put
      .in(jsonBody[UpdateRouteRequest])
      .out(jsonBody[SimpleRouteResponse])
//      .errorOut(jsonBody[String])

  val joinRouteEndpoint =
    baseRouteEndpoint
      .in("join")
      .post
      .in(jsonBody[JoinRouteRequest])
      .out(jsonBody[SimpleRouteResponse])
//      .errorOut(jsonBody[String])

  val getRouteCards =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "cards")
      .get
      .out(jsonBody[GetPointCardsResponse])
//      .errorOut(jsonBody[String])

  val getFavoriteCards =
    baseCardEndpoint
      .in("favorite" / path[Long]("userId"))
      .get
      .out(jsonBody[GetPointCardsResponse])
//      .errorOut(jsonBody[String])

  val getUserRoutes =
    baseRouteEndpoint
      .in("all" / path[Long]("userId"))
      .get
      .out(jsonBody[GetUserRoutesResponse])
//      .errorOut(jsonBody[String])

  val getRoute =
    baseRouteEndpoint
      .in(path[Long]("routeId"))
      .get
      .out(jsonBody[FullRouteResponse])
//      .errorOut(jsonBody[String])

  val getRouteDetailsByDay =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "details" / path[Int]("day"))
      .get
      .out(jsonBody[GetRouteDetailsByDayResponse])
//      .errorOut(jsonBody[String])

  val getRouteMembers =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "members")
      .get
      .out(jsonBody[GetRouteMembersResponse])
//      .errorOut(jsonBody[String])

  val deleteRouteMember =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "member" / path[Long]("userId"))
      .delete
      .out(jsonBody[String])

  val addRouteMember =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "member" / path[Long]("userId"))
      .put
      .out(jsonBody[RouteMemberResponse])

  val addPointToRoute =
    baseRouteEndpoint
      .in("add-to-route")
      .post
      .in(jsonBody[AddPointToRouteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val deletePointFromRoute =
    baseRouteEndpoint
      .in("delete-from-route")
      .delete
      .in(jsonBody[DeletePointFromRouteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val movePointInRoute =
    baseRouteEndpoint
      .in("move")
      .put
      .in(jsonBody[UpdatePointInRouteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val createCard =
    baseCardEndpoint
      .in("create-card")
      .post
      .in(jsonBody[CreateCardRequest])
      .out(jsonBody[SimpleCardResponse])
//      .errorOut(jsonBody[String])

  val copyCard =
    baseCardEndpoint
      .in("copy-card")
      .post
      .in(jsonBody[CreateCardRequest])
      .out(jsonBody[SimpleCardResponse])

  val updateCard =
    baseCardEndpoint
      .in("update")
      .put
      .in(jsonBody[UpdateCardRequest])
      .out(jsonBody[SimpleCardResponse])
//      .errorOut(jsonBody[String])

  val getCard =
    baseCardEndpoint
      .in(path[Long]("cardId"))
      .get
      .out(jsonBody[CreateCardRequest])
//      .errorOut(jsonBody[String])

  val deleteCard =
    baseCardEndpoint
      .in(path[Long]("cardId"))
      .delete
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val getUserRouteCards =
    baseRouteEndpoint
      .in(path[Long]("routeId") / "user-cards" / path[Long]("userId"))
      .get
      .out(jsonBody[GetuserPointCardsResponse])
//      .errorOut(jsonBody[String])

  val copyRoute =
    baseRouteEndpoint
      .in("copy")
      .post
      .in(jsonBody[CopyRequest])
      .out(jsonBody[String])

  val addToFavorite =
    baseCardEndpoint
      .in("favorite" / "add")
      .post
      .in(jsonBody[AddToFavoriteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val deleteFavorite =
    baseCardEndpoint
      .in("favorite" / "del")
      .delete
      .in(jsonBody[AddToFavoriteRequest])
      .out(jsonBody[String])

  val createVote =
    baseVoteEndpoint
      .in("create")
      .post
      .in(jsonBody[CreateVoteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val deleteVote =
    baseVoteEndpoint.delete
      .in(path[Long]("userId") / path[Long]("cardId"))
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val updateVote =
    baseVoteEndpoint.put
      .in(jsonBody[CreateVoteRequest])
      .out(jsonBody[String])
//      .errorOut(jsonBody[String])

  val getVote =
    baseVoteEndpoint
      .in(path[Long]("userId") / path[Long]("cardId"))
      .get
      .out(jsonBody[Vote])
//      .errorOut(jsonBody[String])

  val getVotesForCard =
    baseVoteEndpoint
      .in(path[Long]("routeId") / "for-card" / path[Long]("userId"))
      .get
      .out(jsonBody[VotesForCard])
//      .errorOut(jsonBody[String])

  val getCardsForVote =
    baseVoteEndpoint
      .in(path[Long]("routeId") / "for-vote" / path[Long]("userId"))
      .get
      .out(jsonBody[List[RoutePointCard]])
//      .errorOut(jsonBody[String])

  val registerEndpoint =
    endpoint
      .in("api" / "users")
      .in("sign-up")
      .post
      .in(jsonBody[RegisterRequest])
      .out(jsonBody[AuthResponse])
      .errorOut(jsonBody[String])

  val loginEndpoint =
    endpoint
      .in("api" / "users")
      .post
      .in("login")
      .in(jsonBody[LoginRequest])
      .out(jsonBody[AuthResponse])
      .errorOut(jsonBody[String])

  val getUserEndpoint =
    baseUserEndpoint.get
      .in(path[Long]("userId"))
      .out(jsonBody[UserInfo])
//      .errorOut(jsonBody[String])

  val updateUserEndpoint =
    baseUserEndpoint.put
      .in(jsonBody[UserUpdateRequest])
//      .errorOut(jsonBody[String])

  val deleteUserEndpoint =
    baseUserEndpoint.delete
      .in(path[Int]("userId"))
//      .errorOut(jsonBody[String])

  val getRecPlacesEndpoint =
    baseRecommendationEndpoint.
      get
      .in("place" / path[Long]("UserId"))
      .out(jsonBody[PlaceRecommendationResponse])

  val getRecRoutesEndpoint =
    baseRecommendationEndpoint.
      get
      .in("route" / path[Long]("UserId"))
      .out(jsonBody[RouteRecommendationResponse])


}
