package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json
import util.AuthorizedAction

@Singleton
class DeveloperController @Inject() (
    val controllerComponents: ControllerComponents,
    val authorizedAction: AuthorizedAction
) extends BaseController {
  def stats() = authorizedAction { implicit request => Ok(Json.obj()) }
}
