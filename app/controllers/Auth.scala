package controllers

import play.api.mvc._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._

object Auth extends Controller with MongoController with Secured {
  import User._

  /*
   * Registration
   */
  def signup = WithUser.withEmail { implicit request =>
    // if user is not found in db
    addUser(request.body) flatMap { lastError =>
      if (lastError.ok)
        Created.withSessionToken(request.body) // with new session and token cookies
      else
        Future.successful(BadRequest(lastError.stringify))
    }
  } {
    // if user is found in db
    _ => _ => Future.successful(Forbidden)
  }

  /*
   * Authentication
   */
  def signin = WithUser.withEmailPassword {
    // user is not found in db
    _ => Future.successful(Unauthorized)
  } {
    // user is found in db
    user => _ => Ok.withSessionToken(user) // with new session and token cookies
  }

  def signout = WithAuth { user => _ => Ok.discardingSessionToken(user) }

  /*
   * Authorization
   */
  def authorize = WithAuth { _ => _ => Future.successful { Ok } }

  /* API */

  /*
   * Check if User exists
   * authorization is not required
   */
  def exists(email: String) = Action.async { implicit request =>
    findUserByEmail(Json.obj("email" -> email)) map {
      _ match {
        case None => Ok(Json.obj("isUnique" -> true))
        case Some(user) => Ok(Json.obj("isUnique" -> false))
      }
    }
  }

  /*
   * Get User information
   */
  def getUser = WithAuth { user => _ => Future.successful { Ok(Json.stringify(user)) } }

  /*
   * Create User (just for example)
   */
  def createUser = WithAuth(parse.json) { _ =>
    implicit request =>
      addUser(request.body) map { lastError =>
        if (lastError.ok) Created else BadRequest(lastError.stringify)
      }
  }

  /*
   * Delete User (or Account)
   */
  def removeUser = WithAuth { user =>
    _ =>
      deleteUser(user) flatMap { lastError =>
        if (lastError.ok)
          Ok.discardingSessionToken(user)
        else
          Future.successful { BadRequest(lastError.stringify) }
      }
  }

  /*
   * Transform SimpleResult with session and token cookies
   */
  implicit class ResultWithSessionToken(result: Result) {
    /*
     * Generate authorization token to avoid CSRF attacks
     */
    def xsrfToken = java.util.UUID.randomUUID().toString()

    /*
     * Create a new session with token cookies and save it to db
     */
    def withSessionToken(user: JsValue, token: String = xsrfToken) =
      updateUserTokenByEmail(user, token) map { lastError =>
        if (lastError.ok)
          result
            .withSession("email" -> (user \ "email").as[String]) // play internal session cookies
            .withCookies(
              Cookie("XSRF-TOKEN", token, httpOnly = false), // add external xsrf-token cookie
              Cookie("email", (user \ "email").as[String], httpOnly = false)) // add external email cookie - there is no need to save this cookie on the client side
        else
          Unauthorized(s"Token is not assigned: {{lastError}}")
      }

    /*
     * Discarding the session with token cookies and remove it from db
     */
    def discardingSessionToken(user: JsValue) =
      updateUserTokenByEmail(user).map { lastError =>
        result
          .withNewSession
          .discardingCookies(DiscardingCookie("XSRF-TOKEN"), DiscardingCookie("email")) // clear session and cookies
      }
  }
}

/*
 * User Object
 */
object User {
  /*
   * Simple user object
   * {
   * 	"email": "..",
   *  	"password": "..",
   *   	"info": "..",
   *    "created": ".."	//registration date
   *    "session_token": ".." //authorization token
   * }
   */
  import play.api.Play.current

  implicit lazy val db = ReactiveMongoPlugin.db

  def collection = db.collection[JSONCollection]("user")

  /*
   * find
   */
  def findUser(json: JsValue) =
    collection
      .find(json)
      .cursor[JsObject].collect[List]().map(_.headOption)

  def findUserByEmail(json: JsValue) =
    collection
      .find(Json.obj("email" -> json \ "email"))
      .cursor[JsObject].collect[List]().map(_.headOption)

  def findUserByEmailPassword(json: JsValue) =
    collection
      .find(Json.obj("email" -> json \ "email", "password" -> json \ "password"))
      .cursor[JsObject].collect[List]().map(_.headOption)

  def findUserByEmailToken(json: JsValue) =
    collection
      .find(Json.obj("email" -> json \ "email", "session_token" -> json \ "session_token"))
      .cursor[JsObject].collect[List]().map(_.headOption)

  /*
   * add
   */
  def addUser(json: JsValue, token: String = "") =
    collection.insert(json +++ Json.obj("created" -> new org.joda.time.DateTime(), "session_token" -> token))

  /*
   * update
   */
  def updateUser(json: JsValue, mod: JsValue) =
  // json - selector json query; mod - update json modifier
    collection.update(json, mod)

  def updateUserByEmail(json: JsValue, mod: JsValue) =
    collection.update(Json.obj("email" -> json \ "email"), mod)

  def updateUserByEmailPassword(json: JsValue, mod: JsValue) =
    collection.update(Json.obj("email" -> json \ "email", "password" -> json \ "pasword"), mod)

  def updateUserTokenByEmail(json: JsValue, token: String = "") =
    collection.update(Json.obj("email" -> json \ "email"), Json.obj("$set" -> Json.obj("session_token" -> token)))

  /*
   * delete
   */
  def deleteUser(json: JsValue) = collection.remove(json)

  def deleteUserByEmail(json: JsValue) =
    collection.remove(Json.obj("email" -> json \ "email"))

  def deleteUserByEmailPassword(json: JsValue) =
    collection.remove(Json.obj("email" -> json \ "email", "password" -> json \ "pasword"))

  /*
   * utils
   */
  implicit class addJson(_json: JsValue) {
    // add JsObject to JsValue
    def +++(json: JsObject): JsValue = {
      _json.transform(__.json.update(__.read[JsObject].map(_ ++ json))).getOrElse(_json)
    }
  }
}

/*
 * Security trait
 */
trait Secured extends Controller {
  import User._

  /*
   * Authorization
   * by "email" and "token" session parameter
   */
  object WithAuth {
    // By default use AnyContent parser
    def apply(f: JsObject => Request[AnyContent] => Future[Result]): Action[AnyContent] = WithAuth(parse.anyContent)(f)

    def apply[T](p: BodyParser[T])(f: JsObject => Request[T] => Future[Result]) =
      Action.async(p) { implicit request =>
        {
          for {
            email <- request.session.get("email")
            session_token <- request.headers.get("X-XSRF-TOKEN")
          } yield for {
            optionuser <- findUserByEmailToken(Json.obj("email" -> email, "session_token" -> session_token))
            result <- optionuser match {
              case None => Future.successful(Unauthorized)
              case Some(user) => f(user)(request)
            }
          } yield result
        } getOrElse {
          Future.successful(Unauthorized)
        }
      }
  }

  /*
   * Authentication
   * by json body that include {"email": "..."} or {"email": "...", "password": "..."} or some else
   */
  object WithUser {
    def apply(getUser: JsValue => Future[Option[JsObject]])(withoutUser: Request[JsValue] => Future[Result] = _ => Future.successful(NotFound))(withUser: JsObject => Request[JsValue] => Future[Result]) =
      Action.async(parse.json) { implicit request =>
        for {
          optionuser <- getUser(request.body) //some function that is used for getting user (i.e., get by email)
          result <- optionuser match {
            case None => withoutUser(request)
            case Some(user) => withUser(user)(request)
          }
        } yield result
      }

    // with email pattern
    def withEmail(withoutUser: Request[JsValue] => Future[Result] = _ => Future.successful(NotFound))(withUser: JsObject => Request[JsValue] => Future[Result]): Action[JsValue] =
      WithUser(findUserByEmail(_))(withoutUser)(withUser)

    // with email, password pattern
    def withEmailPassword(withoutUser: Request[JsValue] => Future[Result] = _ => Future.successful(NotFound))(withUser: JsObject => Request[JsValue] => Future[Result]): Action[JsValue] =
      WithUser(findUserByEmailPassword(_))(withoutUser)(withUser)
  }
}
