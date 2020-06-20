package authorizer

import javax.inject._
import controllers.CreateOrLoginUserDTO
import play.api.libs.json.JsValue
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import com.google.inject.ImplementedBy
import user.UserRepository
import scala.util.Success
import play.api.libs.json.JsObject
import play.api.Configuration
import java.time.Clock
import pdi.jwt.JwtJson
import pdi.jwt.JwtAlgorithm
import user.User
import util.Hash

@ImplementedBy(classOf[AuthorizerImpl])
trait Authorizer {
  def authorize(
      userInfo: CreateOrLoginUserDTO
  ): Future[Option[JwtToken]];
  def deauthorize(email: String): Future[Unit];
}

@Singleton
case class AuthorizerImpl @Inject() (val userRepository: UserRepository)
    extends Authorizer {

  def authorize(loginInfo: CreateOrLoginUserDTO) = {
    for {
      userOption <- userRepository.findByEmail(loginInfo.email)
    } yield {
      userOption match {
        case Some(user) => maybeGenerateJwtToken(user, loginInfo.password)
        case None       => None
      }
    }
  }

  private def maybeGenerateJwtToken(
      user: User,
      rawLoginPassword: String
  ): Option[JwtToken] = {
    val isPasswordCorrect = user.password == Hash(rawLoginPassword)

    if (isPasswordCorrect) {
      Some(JwtToken(Json.obj("email" -> user.email)))
    } else {
      None
    }
  }

  // TODO
  def deauthorize(email: String) = Future {}
}

case class JwtToken(claimData: JsObject) {
  private implicit val clock: Clock = Clock.systemUTC
  private val algorithm = JwtAlgorithm.HS256
  private val secretKey = "changeme"

  private val token: String = JwtJson.encode(
    claimData,
    secretKey,
    algorithm
  )

  override def toString() = token

  def toJson() = Json.obj("type" -> "Bearer", "token" -> token)
}
