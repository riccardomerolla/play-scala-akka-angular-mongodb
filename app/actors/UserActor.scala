package actors

import akka.actor.Actor
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json.JsValue
import play.api.libs.iteratee.Concurrent.Channel
import controllers.Application

/**
 * @author Riccardo Merolla
 *         Created on 27/07/14.
 */
class UserActor (channel: Channel[JsValue]) extends Actor {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  def receive = {
    case message: JsValue =>
      logger.info(s"Sending Message: $message")
      channel.push(message)
  }


}
