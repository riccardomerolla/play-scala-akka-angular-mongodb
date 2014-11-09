package actors

import akka.actor.Actor
import play.api.Logger
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.JsValue

/**
 * @author Riccardo Merolla
 *         Created on 27/07/14.
 */
class UserActor (channel: Channel[JsValue]) extends Actor {

  private final val logger = Logger

  def receive = {
    case message: JsValue =>
      logger.info(s"Sending Message: $message")
      channel.push(message)
  }


}
