package io.johnmurray.router.config

import akka.actor.Actor
import akka.event.Logging

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
      case LoadConfig => {
         if (loadConfig) {
            sender ! ConfigLoaded
         } else {
            sender ! ConfigLoadFailed
         }
      }
      case any => {
         log.warning(s"Unknown message received to config loader actor: $any")
      }
   }


   def loadConfig : Boolean = {
      // todo: implement method
      true
   }

}


object ConfigLoaderActor {
   case object LoadConfig
   case object ConfigLoaded
   case object ConfigLoadFailed
}
