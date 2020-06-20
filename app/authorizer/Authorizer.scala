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
import scala.util.Try

@ImplementedBy(classOf[AuthorizerImpl])
trait Authorizer {
  def authorize(
      userInfo: CreateOrLoginUserDTO
  ): Future[Option[AuthorizationToken]];
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
  ): Option[AuthorizationToken] = {
    val isPasswordCorrect = user.password == Hash(rawLoginPassword)

    if (isPasswordCorrect) {
      Some(AuthorizationToken(user.email))
    } else {
      None
    }
  }

  def deauthorize(email: String) = Future {}
}

case class AuthorizationToken(email: String) {
  private implicit val clock: Clock = Clock.systemUTC

  def toJson(): JsValue = Json.obj("type" -> "Bearer", "token" -> toString())

  override def toString(): String = JwtJson.encode(
    Json.obj("email" -> email),
    AuthorizationToken.secretKey,
    AuthorizationToken.algorithm
  )
}

object AuthorizationToken {
  private val algorithm = JwtAlgorithm.HS256
  private val secretKey = "changeme"

  def apply(obj: JsObject): Try[AuthorizationToken] = {
    val email = (obj \ "email")
      .getOrElse(throw new Exception("Cannot parse JWT token"))
      .toString()
    Success(AuthorizationToken(email))
  }

  def decode(token: String): Try[AuthorizationToken] =
    JwtJson
      .decodeJson(token, secretKey, Seq(algorithm))
      .flatMap(AuthorizationToken(_))
}
