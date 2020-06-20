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

class UserControllerSpec extends PlaySpecification with Results {
  def fakeSignupRequest(body: JsValue = null)(implicit app: Application) =
    route(app, FakeRequest(PUT, "/user").withJsonBody(body))

  def fakeLoginRequest(body: JsValue = null)(implicit app: Application) =
    route(app, FakeRequest(POST, "/user").withJsonBody(body))

  "create" should {
    "return 400 status code if any required field is missing" in new WithApplication {
      def expect400Code(body: JsValue) = {
        val Some(result) =
          fakeSignupRequest(body)
        status(result) must equalTo(400)
      }

      expect400Code(null)
      expect400Code(Json.obj("email" -> "brais"))
      expect400Code(Json.obj("password" -> "doe"))
      expect400Code(Json.obj())
      expect400Code(Json.arr())
    }

    "return a jwt token after a succesful signup" in new WithApplication {
      val requestBody = Json.obj("email" -> "john", "password" -> "doe")
      val Some(result) = fakeSignupRequest(requestBody)
      status(result) must equalTo(200)
      contentAsJson(result) must equalTo(
        Json
          .obj(
            "email" -> "john",
            "password" -> Hash("doe")
          )
      )
    }
  }

  "login" should {
    "return 400 status code if any required field is missing" in new WithApplication {
      def expect400Code(body: JsValue) = {
        val Some(result) =
          fakeLoginRequest(body)
        status(result) must equalTo(400)
      }

      expect400Code(null)
      expect400Code(Json.obj("email" -> "brais"))
      expect400Code(Json.obj("password" -> "doe"))
      expect400Code(Json.obj())
      expect400Code(Json.arr())
    }

    "log user in if the user was previously created" in new WithApplication {
      def expect400Code(body: JsValue) = {
        val Some(result) =
          fakeLoginRequest(body)
        status(result) must equalTo(400)
        contentAsJson(result) must equalTo(
          Json.obj("error" -> "Wrong credentials")
        )
      }
      val userData = Json.obj("email" -> "brais", "password" -> "doe")

      expect400Code(userData)

      // Given I create a user
      val Some(signupResult) = fakeSignupRequest(userData)
      status(signupResult) must equalTo(200)

      // Then it should fail if I try to log the user in with the wrong credentials
      expect400Code(
        Json.obj("email" -> "brais", "password" -> "wrong")
      )
      expect400Code(
        Json.obj("email" -> "braisssss", "password" -> "doe")
      )

      // And it should pass if I use the correct credentials
      val Some(successfulLoginResult) = fakeLoginRequest(userData)
      status(successfulLoginResult) must equalTo(200)

      val response = contentAsJson(successfulLoginResult)
      (response \ "type").as[String] must equalTo("Bearer")
      (response \ "token").as[String] must not be empty
    }
  }
}
