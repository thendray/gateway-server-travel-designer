package clients

import clients.RecommendationClient.PlaceRecommendationResponse
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import model.User._
import model.response.RouteRecommendationResponse
import sttp.client3._
import sttp.client3.circe._
import zio.{Task, _}

class RecommendationClient(baseUrl: String, backend: SttpBackend[Task, Any]) {

  def getPlaces(userId: Long): Task[PlaceRecommendationResponse] = {
    val request = basicRequest
      .get(uri"$baseUrl/api/recommendation/place/$userId")
      .response(asJson[PlaceRecommendationResponse])

    request.send(backend).flatMap { response =>
      response.body match {
        case Right(places) =>
          ZIO.succeed {
            places
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to fetch recommendation: $error"))
      }
    }
  }

  def getRoutes(userId: Long): Task[RouteRecommendationResponse] = {
    val request = basicRequest
      .get(uri"$baseUrl/api/recommendation/route/$userId")
      .response(asJson[RouteRecommendationResponse])

    request.send(backend).flatMap { response =>
      response.body match {
        case Right(routes) =>
          ZIO.succeed {
            routes
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to fetch recommendation: $error"))
      }
    }
  }

}

object RecommendationClient {

  val live: URLayer[SttpBackend[Task, Any], RecommendationClient] =
    ZLayer.fromFunction((b: SttpBackend[Task, Any]) => new RecommendationClient("http://51.250.77.99:8084", b))

  case class PlaceRecommendationResponse(cards: List[PlaceRecommendation])

  case class PlaceRecommendation(
      id: Long,
      name: String,
      author: String,
      description: String,
      category: String,
      photo: String)

  implicit val decoder: Decoder[PlaceRecommendationResponse] = deriveDecoder[PlaceRecommendationResponse]
  implicit val encoder: Encoder[PlaceRecommendationResponse] = deriveEncoder[PlaceRecommendationResponse]

  implicit val decoder2: Decoder[PlaceRecommendation] = deriveDecoder[PlaceRecommendation]
  implicit val encoder2: Encoder[PlaceRecommendation] = deriveEncoder[PlaceRecommendation]
}
