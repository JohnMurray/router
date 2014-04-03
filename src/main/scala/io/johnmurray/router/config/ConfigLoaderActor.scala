package io.johnmurray.router.config

import akka.actor.Actor
import akka.event.Logging
import org.parboiled.errors.ParsingException
import spray.json.JsonParser
import RouterJsonProtocols._
import scala.concurrent.duration._

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * Actor to run in the background and periodically (re)load the config. When we're talking
 * about loading configs, there are three different configurations that need to be loaded.
 *
 * The first two configs are the application config and the application config override. The
 * main application config is packaged as a static resource. The override config can either
 * be in a default location, or in a custom location if provided via a system property.
 */
class ConfigLoaderActor extends Actor {

   import ConfigLoaderActor._

   val log = Logging(context.system, this)

   def receive = {
      case LoadConfig =>
         log.info("Loading app-configuration")
         try {
            ConfigStore.config = loadBaseConfig
            sender ! ConfigLoaded
            context.system.scheduler.schedule(0.seconds, 30.seconds, self, ReLoadRoutes)
            log.info("Scheduled route-loading")
         }
         catch {
            case Throwable => sender ! ConfigLoadFailed
         }

      case ReLoadRoutes =>
         log.info("Loading routes")

      case any =>
         log.warning(s"Unknown message received to config loader actor: $any")
   }



   /**
    * Loads the base config and attempts to load the overrides if it is available
    * and can be read/parsed.
    *
    * @return An application config
    */
   def loadBaseConfig: Config = {
      val configContent: String = scala.io.Source.fromFile(
         this.getClass.getClassLoader.getResource("reference.json").getFile).mkString

      var config = JsonParser(configContent).convertTo[Config]

      getOverrideConfigLocation(config).foreach { path =>
         try {
            val overrideConfigContent = scala.io.Source.fromFile(path).mkString
            val overrideConfig = JsonParser(overrideConfigContent).convertTo[OverrideConfig]
            config = config.merge(overrideConfig)
         }
         catch {
            case ex: ParsingException => {
               log.warning(s"Could not parse override config: ${ex.getMessage}")
            }
            case ex: Throwable => {
               log.warning(s"Could not load override config: ${ex.getMessage}")
            }
         }
      }

      config
   }


   /*
    * Returns the location of the override config if it exists (on the Filesystem)
    */
   def getOverrideConfigLocation(config: Config): Option[String] = {
      val path = sys.props.get("overrideConfLocation").getOrElse(config.overrideConfigurationLocation)
      val file = new java.io.File(path)

      if (file.exists && file.canRead) {
         Some(path)
      }
      else {
         log.warning(s"no override configuration found at '$path'")
         None
      }
   }

}


object ConfigLoaderActor {
   case object ConfigLoaded
   case object ConfigLoadFailed
   case object LoadConfig
   case object ReLoadRoutes
}
