package actors

import akka.actor.Actor
import org.slf4j.{Logger, LoggerFactory}
import services.UUIDGenerator

/**
 * @author Riccardo Merolla
 *         Created on 27/07/14.
 */
class UUIDActor (uuidGenerator: UUIDGenerator) extends Actor {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[App])

  def receive = {
    case Some =>
      logger.info("Generate UUID")
      sender ! uuidGenerator.generate.toString
  }

}
