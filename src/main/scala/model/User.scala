package model

class User {}

object User {


  case class RegisterRequest(
      email: String,
      username: String,
      password: String,
      profilePhoto: String)

  case class LoginRequest(
      email: String,
      password: String)

  case class AuthResponse(
      userId: Int,
      jwt: String)

  case class UserResponse(
      userId: Int,
      email: String,
      username: String,
      profilePhoto: String)

  case class UserUpdateRequest(
      userId: Int,
      email: Option[String],
      username: Option[String],
      profilePhoto: Option[String])
}
