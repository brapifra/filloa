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
class DeveloperController @Inject() (
    val controllerComponents: ControllerComponents,
    val authorizedAction: AuthorizedAction
) extends BaseController {
  def stats() = authorizedAction { implicit request => Ok(Json.obj()) }
}
