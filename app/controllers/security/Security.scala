package controllers.security

import play.api.cache._
import play.api.libs.json._
import play.api.mvc._

/**
 * Created by ricky on 05/10/14.
 */
/**
 * Security actions that should be used by all controllers that need to protect their actions.
 * Can be composed to fine-tune access control.
 */
trait Security { self: Controller =>

  implicit val app: play.api.Application = play.api.Play.current

  val AuthTokenHeader = "X-XSRF-TOKEN"
  val AuthTokenCookieKey = "XSRF-TOKEN"
  val AuthTokenUrlKey = "auth"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => Long => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      val maybeToken = request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey))
      maybeToken flatMap { token =>
        Cache.getAs[Long](token) map { userid =>
          f(token)(userid)(request)
        }
      } getOrElse Unauthorized(Json.obj("err" -> "No Token"))
    }

}

case class Login(email: String, password: String)