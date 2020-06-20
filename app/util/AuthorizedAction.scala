package util

import authorizer.AuthorizationToken
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import javax.inject._
import play.api.mvc._
import scala.util.Success

class AuthorizedRequest[A](val token: AuthorizationToken, request: Request[A])
    extends WrappedRequest[A](request)

class AuthorizedAction @Inject() (parser: BodyParsers.Default)(
    implicit ec: ExecutionContext
) extends ActionBuilder[AuthorizedRequest, AnyContent] {
  override def parser: BodyParser[AnyContent] = parser
  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](
      request: Request[A],
      block: (AuthorizedRequest[A]) => Future[Result]
  ): Future[Result] = {
    val authorizationToken = request.headers
      .get("Authorization")
      .map(_.replace("Bearer ", ""))
      .map(AuthorizationToken.decode(_))

    authorizationToken match {
      case Some(Success(token)) =>
        block(new AuthorizedRequest(token, request))
      case _ => Future.successful(Results.Unauthorized);
    }
  }
}
