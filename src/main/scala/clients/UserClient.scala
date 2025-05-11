package clients

import clients.UserClient.UserInfo
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import model.User._
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import sttp.client3.circe._
import zio.{Task, _}

class UserClient(baseUrl: String, backend: SttpBackend[Task, Any]) {

  def getUserById(userId: Long): Task[UserInfo] = {
    val request = basicRequest
      .get(uri"$baseUrl/users/$userId")
      .response(asJson[UserInfo])

    request.send(backend).flatMap { response =>
      response.body match {
        case Right(userInfo) =>
          ZIO.succeed {
//            println(s"Get user info $userInfo")
            userInfo
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to fetch user: $error"))
      }
    }
  }

  def register(request: RegisterRequest): Task[AuthResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/users/sign-up")
      .body(request)
      .response(asJson[AuthResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(authResponse) =>
          ZIO.succeed {
            println(s"Successfully registered: $authResponse")
            authResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to register: $error"))
      }
    }
  }

  def login(request: LoginRequest): Task[AuthResponse] = {
    val sttpRequest = basicRequest
      .post(uri"$baseUrl/users/login")
      .body(request)
      .response(asJson[AuthResponse])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(authResponse) =>
          ZIO.succeed {
            println(s"Successfully logged in: $authResponse")
            authResponse
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to login: $error"))
      }
    }
  }

  def updateUser(request: UserUpdateRequest): Task[Unit] = {
    val sttpRequest = basicRequest
      .put(uri"$baseUrl/users")
      .body(request)

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(_) =>
          ZIO.succeed {
            println("User updated successfully")
          }
        case Left(error) =>
          println(s"Failed to update user: $error")
          ZIO.fail(new RuntimeException(s"Failed to update user: $error"))
      }
    }
  }

  def deleteUser(userId: Int): Task[Unit] = {
    val sttpRequest = basicRequest
      .delete(uri"$baseUrl/users/$userId")
      .response(asJson[Unit])

    sttpRequest.send(backend).flatMap { response =>
      response.body match {
        case Right(_) =>
          ZIO.succeed {
            println("User deleted successfully")
          }
        case Left(error) => ZIO.fail(new RuntimeException(s"Failed to delete user: $error"))
      }
    }
  }

}

object UserClient {

  val live: URLayer[SttpBackend[Task, Any], UserClient] =
    ZLayer.fromFunction((b: SttpBackend[Task, Any]) =>
      new UserClient("http://localhost:8081/api", b)
    )

  case class UserInfo(userId: Int, email: String, username: String, profilePhoto: String)

  implicit val decoder: Decoder[UserInfo] = deriveDecoder[UserInfo]
  implicit val encoder: Encoder[UserInfo] = deriveEncoder[UserInfo]
}
