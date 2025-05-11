package clients

import io.circe.generic.auto._
import model.request._
import model.response.GetRouteMembersResponse.RouteMemberResponse
import model.response.{
  FullRouteResponse,
  GetPointCardsResponse,
  GetRouteDetailsByDayResponse,
  GetRouteMembersResponse,
  GetUserRoutesResponse,
  GetuserPointCardsResponse,
  SimpleCardResponse,
  SimpleRouteResponse,
  VotesForCard
}
import model.{Route, RoutePointCard, Vote}
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.circe._
import sttp.client3.{basicRequest, SttpBackend, UriContext}
import zio._

class MainClient(baseUrl: String, backend: SttpBackend[Task, Any]) {

  def createVote(request: CreateVoteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/vote/create")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Vote created successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to create vote: $error"))
      }
    }
  }

  def deleteVote(userId: Long, cardId: Long): Task[String] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/vote/$userId/$cardId")
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Vote deleted successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to delete vote: $error"))
      }
    }
  }

  def updateVote(request: CreateVoteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/vote")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Vote updated successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to update vote: $error"))
      }
    }
  }

  def getVote(userId: Long, cardId: Long): Task[Vote] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/vote/$userId/$cardId")
      .response(asJson[Vote])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(vote) =>
          ZIO.succeed {
            println(s"Retrieved vote: $vote")
            vote
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get vote: $error"))
      }
    }
  }

  def getVotesForCard(routeId: Long, cardId: Long): Task[VotesForCard] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/vote/$routeId/for-card/$cardId")
      .response(asJson[VotesForCard])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(votes) =>
          ZIO.succeed {
            println(s"Retrieved ${votes.votes.size} votes")
            votes
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get cards with user votes: $error"))
      }
    }
  }

  def getCardsForVote(routeId: Long, userId: Long): Task[List[RoutePointCard]] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/vote/$routeId/for-vote/$userId")
      .response(asJson[List[RoutePointCard]])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(cards) =>
          ZIO.succeed {
            println(s"Retrieved ${cards.size} cards for vote")
            cards
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get cards for vote: $error"))
      }
    }
  }

  def createCard(request: CreateCardRequest): Task[SimpleCardResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/card/create-card")
      .body(request)
      .response(asJson[SimpleCardResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(cardResponse) =>
          ZIO.succeed {
//            println(s"Card created successfully: $cardResponse")
            cardResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to create card: $error"))
      }
    }
  }

  def copyCard(request: CreateCardRequest): Task[SimpleCardResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/card/copy-card")
      .body(request)
      .response(asJson[SimpleCardResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(cardResponse) =>
          ZIO.succeed {
            cardResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to copy card: $error"))
      }
    }
  }

  def updateCard(request: UpdateCardRequest): Task[SimpleCardResponse] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/card/update")
      .body(request)
      .response(asJson[SimpleCardResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(cardResponse) =>
          ZIO.succeed {
//            println(s"Card updated successfully: $cardResponse")
            cardResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to update card: $error"))
      }
    }
  }

  def getCard(cardId: Long): Task[CreateCardRequest] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/card/$cardId")
      .response(asJson[CreateCardRequest])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(card) =>
          ZIO.succeed {
//            println(s"Retrieved card: $card")
            card
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get card: $error"))
      }
    }
  }

  def deleteCard(cardId: Long): Task[String] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/card/$cardId")
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Card deleted successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to delete card: $error"))
      }
    }
  }

  def getUserRouteCards(routeId: Long, userId: Long): Task[GetuserPointCardsResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/$routeId/user-cards/$userId")
      .response(asJson[GetuserPointCardsResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(pointCardsResponse) =>
          ZIO.succeed {
            println(s"Retrieved user route cards successfully")
            pointCardsResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get user route cards: $error"))
      }
    }
  }

  def copyRoute(request: CopyRequest): Task[String] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/route/copy")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Copy route successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to copy route: $error"))
      }
    }
  }

  def addToFavorite(request: AddToFavoriteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/card/favorite/add")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Added to favorites successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to add to favorites: $error"))
      }
    }
  }

  def deleteFavorite(request: AddToFavoriteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/card/favorite/del")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Delete favorite successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to add to favorites: $error"))
      }
    }
  }

  def createRoute(route: Route): Task[SimpleRouteResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/route/create")
      .body(route)
      .response(asJson[SimpleRouteResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(routeResponse) =>
          ZIO.succeed {
            println(s"Route created successfully: $routeResponse")
            routeResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to create route: $error"))
      }
    }
  }

  def addSettings(request: ExtraSettings): Task[String] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/route/add/settings")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(response) =>
          ZIO.succeed {
            println(s"Add settings successfully: $response")
            response
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to create route: $error"))
      }
    }
  }

  def updateRoute(request: UpdateRouteRequest): Task[SimpleRouteResponse] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/route/update")
      .body(request)
      .response(asJson[SimpleRouteResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(routeResponse) =>
          ZIO.succeed {
            println(s"Route updated successfully: $routeResponse")
            routeResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to update route: $error"))
      }
    }
  }

  def joinRoute(request: JoinRouteRequest): Task[SimpleRouteResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/route/join")
      .body(request)
      .response(asJson[SimpleRouteResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(routeResponse) =>
          ZIO.succeed {
            println(s"Joined route successfully: $routeResponse")
            routeResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to join route: $error"))
      }
    }
  }

  def getRouteCards(routeId: Long): Task[GetPointCardsResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/$routeId/cards")
      .response(asJson[GetPointCardsResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(pointCardsResponse) =>
          ZIO.succeed {
            println(s"Retrieved route cards successfully")
            pointCardsResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get route cards: $error"))
      }
    }
  }

  def getFavoriteCards(userId: Long): Task[GetPointCardsResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/card/favorite/$userId")
      .response(asJson[GetPointCardsResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(pointCardsResponse) =>
          ZIO.succeed {
            println(s"Retrieved favorite cards successfully")
            pointCardsResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get favorite cards: $error"))
      }
    }
  }

  def getUserRoutes(userId: Long): Task[GetUserRoutesResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/all/$userId")
      .response(asJson[GetUserRoutesResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(userRoutesResponse) =>
          ZIO.succeed {
            println(s"Retrieved user routes successfully")
            userRoutesResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get user routes: $error"))
      }
    }
  }

  def getRoute(routeId: Long): Task[FullRouteResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/$routeId")
      .response(asJson[FullRouteResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(routeResponse) =>
          ZIO.succeed {
            println(s"Retrieved route successfully: $routeResponse")
            routeResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get route: $error"))
      }
    }
  }

  def getRouteDetailsByDay(routeId: Long, day: Int): Task[GetRouteDetailsByDayResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/$routeId/details/$day")
      .response(asJson[GetRouteDetailsByDayResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(detailsResponse) =>
          ZIO.succeed {
            println(s"Retrieved route details for day $day successfully")
            detailsResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get route details: $error"))
      }
    }
  }

  def getRouteMembers(routeId: Long): Task[GetRouteMembersResponse] = {
    val sttpRequest = basicRequest
      .get(uri"$baseUrl/route/$routeId/members")
      .response(asJson[GetRouteMembersResponse])
    println("Try to get route members")

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(membersResponse) =>
          ZIO.succeed {
            println(s"Retrieved route members successfully")
            membersResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to get route members: $error"))
      }
    }
  }

  def deleteRouteMember(routeId: Long, userId: Long): Task[String] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/route/$routeId/member/$userId")
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(response) =>
          ZIO.succeed {
            response
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to delete route member: $error"))
      }
    }
  }

  def addRouteMember(routeId: Long, userId: Long): Task[RouteMemberResponse] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/route/$routeId/member/$userId")
      .response(asJson[RouteMemberResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(response) =>
          ZIO.succeed {
            response
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to add route member: $error"))
      }
    }
  }

  def addPointToRoute(request: AddPointToRouteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/route/add-to-route")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Point added to route successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to add point to route: $error"))
      }
    }
  }

  def deletePointFromRoute(request: DeletePointFromRouteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/route/delete-from-route")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Point deleted from route successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to delete point from route: $error"))
      }
    }
  }

  def movePointInRoute(request: UpdatePointInRouteRequest): Task[String] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/route/move")
      .body(request)
      .response(asJson[String])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(result) =>
          ZIO.succeed {
            println(s"Point moved in route successfully: $result")
            result
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to move point in route: $error"))
      }
    }
  }
}

object MainClient {

  val live: URLayer[SttpBackend[Task, Any], MainClient] =
    ZLayer.fromFunction((b: SttpBackend[Task, Any]) => new MainClient("http://51.250.77.99:8082/api", b))
}
