package controllers

import play.api.mvc._
import play.api.test._

import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.Application
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.JsUndefined
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsDefined
import util.Hash
import authorizer.Authorizer
import authorizer.AuthorizerImpl
import authorizer.AuthorizationToken

class DeveloperControllerSpec extends PlaySpecification with Results {
  def fakeDeveloperStatsRequest(jwtToken: String)(implicit app: Application) =
    route(
      app,
      FakeRequest(GET, "/developer/stats").withHeaders(
        "Authorization" -> jwtToken
      )
    )

  "stats" should {
    "require a valid authorization token" in new WithApplication {
      def expect401Code(jwtToken: String) = {
        val Some(result) =
          fakeDeveloperStatsRequest(jwtToken)
        status(result) must equalTo(401)
      }

      expect401Code("")
      expect401Code("1234")
      expect401Code(AuthorizationToken("john@email.com").toString() + "x")
    }
    "return an empty object if user is logged in" in new WithApplication {
      def expect200Code(jwtToken: String) = {
        val Some(result) =
          fakeDeveloperStatsRequest(jwtToken)
        status(result) must equalTo(200)
      }

      val token = AuthorizationToken("john@email.com")
      val Some(result) = fakeDeveloperStatsRequest(token.toString())
      status(result) must equalTo(200)
      contentAsJson(result) must equalTo(Json.obj())
    }
  }
}
