package io.johnmurray.router.config

import akka.actor.Actor
import akka.event.Logging
import org.parboiled.errors.ParsingException

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * Actor to run in the background and periodically (re)load the config
 */
class ConfigLoaderActor extends Actor {

   import ConfigLoaderActor._

   val log = Logging(context.system, this)

   def receive = {
      case LoadConfig => self.tell((LoadConfig, false), sender())
      case (LoadConfig, returnStatus: Boolean) => {
         log.info("(Re)Loading configuration files")
         val loaded = loadConfig

         if (returnStatus) {
            if (loaded) sender ! ConfigLoaded
            else sender ! ConfigLoadFailed
         }
      }
      case any => {
         log.warning(s"Unknown message received to config loader actor: $any")
      }
   }


   def loadConfig : Boolean = {
      import RouterJsonProtocols._
      import spray.json._

      try {
         val configContent: String = scala.io.Source.fromFile(
            this.getClass.getClassLoader.getResource("reference.json").getFile).mkString

         val config = JsonParser(configContent).convertTo[Config]
         ConfigStore.config = config
      } catch {
         case ex: ParsingException => {
            log.error(ex, "Could not load configuration")
            return false
         }
      }
      true
   }

}


object ConfigLoaderActor {
   case object LoadConfig
   case object ConfigLoaded
   case object ConfigLoadFailed
}
