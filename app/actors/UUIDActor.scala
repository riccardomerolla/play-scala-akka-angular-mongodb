package actors

import akka.actor.Actor
import org.slf4j.{Logger, LoggerFactory}
import services.UUIDGenerator
import controllers.Application

/**
 * @author Riccardo Merolla
 *         Created on 27/07/14.
 */
class UUIDActor (uuidGenerator: UUIDGenerator) extends Actor {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  def receive = {
    case Some =>
      logger.info("Generate UUID")
      sender ! uuidGenerator.generate.toString
  }

}
