package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsUndefined
import play.api.libs.json.JsObject
import play.api.libs.json.JsonNaming
import play.api.libs.json.Format
import authorizer.Authorizer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import user.UserFacade
import util.AuthorizedAction

@Singleton
class UserController @Inject() (
    val controllerComponents: ControllerComponents,
    val userFacade: UserFacade,
    val authorizer: Authorizer,
    val authorizedAction: AuthorizedAction,
) extends BaseController {

  def create() = Action.async(parse.json[CreateOrLoginUserDTO]) {
    implicit request: Request[CreateOrLoginUserDTO] =>
      userFacade
        .createUser(request.body)
        .map(newUser => Ok(Json.toJson(newUser)))

  }

  def login() = Action.async(parse.json[CreateOrLoginUserDTO]) {
    implicit request: Request[CreateOrLoginUserDTO] =>
      for {
        tokenOption <- authorizer
          .authorize(request.body)
      } yield {
        tokenOption match {
          case Some(token) =>
            Ok(token.toJson())
          case None =>
            BadRequest(Json.obj("error" -> "Wrong credentials"))
        }
      }
  }

  def stats() = authorizedAction { implicit request =>
    Ok(Json.obj())
  }
}

case class CreateOrLoginUserDTO(email: String, password: String) {}
object CreateOrLoginUserDTO {
  implicit val format: Format[CreateOrLoginUserDTO] =
    Json.format[CreateOrLoginUserDTO]
}
