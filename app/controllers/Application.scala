package controllers

import javax.inject.{Inject, Singleton}

import actors.{UUIDActor, UserActor}
import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.json.JsValue
import play.api.mvc._
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
import play.libs.Akka
import services.UUIDGenerator

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator) extends Controller {

  implicit val app: play.api.Application = play.api.Play.current

  private final val logger = Logger

  lazy val CacheExpiration =
    app.configuration.getInt("cache.expiration").getOrElse(60 /*seconds*/ * 2 /* minutes */)

  implicit val timeout = Timeout(5 seconds)

  lazy val uuidActor : ActorRef = Akka.system.actorOf(Props(new UUIDActor(uuidGenerator)))

  def index = GetAction { request => {
      val userSession = request.session.get("csrfToken").map(i => i.split("-")(0)).getOrElse("")
      logger.info(s"$userSession")
      logger.info("Serving index page...")
      Ok(views.html.index())
    }
  }

  def randomUUID = Action.async {
    logger.info("calling UUIDGenerator...")
    (uuidActor ? Some) map { case x: String => Ok(x) }
  }

  def ws = WebSocket.using[JsValue] { request =>
    logger.info("User Connected")

    val (out, channel) = Concurrent.broadcast[JsValue]

    val userActor = Akka.system.actorOf(Props(classOf[UserActor], channel))

    val in = Iteratee.foreach[JsValue] { message =>
      logger.info(s"Received message: $message")
      userActor ! message
    }.map { _ =>
      logger.info("User Disconnected")
      Akka.system.stop(userActor)
    }

    (in, out)
  }

  object PostAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      // authentication code here
      block(request)
    }
    override def composeAction[A](action: Action[A]) = CSRFCheck(action)
  }

  object GetAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      //val userSession = CSRF.getToken(request).map(_.value).getOrElse("")
      //logger.info(s"GetAction $userSession")
      // authentication code here
      block(request)
    }
    override def composeAction[A](action: Action[A]) = CSRFAddToken(action)
  }

}
