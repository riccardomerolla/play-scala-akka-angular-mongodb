package actors

import akka.actor.Actor
import play.api.Logger
import services.UUIDGenerator

/**
 * @author Riccardo Merolla
 *         Created on 27/07/14.
 */
class UUIDActor (uuidGenerator: UUIDGenerator) extends Actor {

  private final val logger = Logger

  def receive = {
    case Some =>
      logger.info("Generate UUID")
      sender ! uuidGenerator.generate.toString
  }

}
