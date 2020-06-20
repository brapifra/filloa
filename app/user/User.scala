package user

import play.api.libs.json.Writes
import play.api.libs.json.Json

case class User(email: String, password: String) {}

object User {
  implicit val userWrites: Writes[User] = new Writes[User] {
    def writes(user: User) = Json.obj(
      "email" -> user.email,
      "password" -> user.password
    )
  }
}
