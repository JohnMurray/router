package io.johnmurray.router.config

import akka.actor.{Cancellable, Actor}
import akka.event.Logging

import io.johnmurray.router.route.RouteMatcher

import java.nio.file.{Files, Paths}

import org.parboiled.errors.ParsingException

import RouterJsonProtocols._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json.DefaultJsonProtocol._
import spray.json.JsonParser


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
   var routeSchedule: Option[Cancellable] = None

   val reloadInterval = 30.seconds

   def receive = {
      case LoadConfig =>
         log.info("Loading app-configuration")
         try {
            ConfigStore.config = loadBaseConfig
            sender ! ConfigLoaded

            if (routeSchedule.isEmpty || routeSchedule.exists(_.isCancelled)) {
               log.info(s"Scheduled route-loading (interval: $reloadInterval)")
               routeSchedule = Some(
                  context.system.scheduler.schedule(0.seconds, reloadInterval, self, ReLoadRoutes))
            }
         }
         catch {
            case t: Throwable =>
               log.error(s"Could not load config: $t")
               sender ! ConfigLoadFailed
         }

      case ReLoadRoutes =>
         val routes = loadRouteConfig(ConfigStore.config)
         ConfigStore.routeMatcher = RouteMatcher(routes.toSet)

      case any =>
         log.warning(s"Unknown message received to config loader actor: $any")
   }


   /**
    * Given the base-config, find the routes file, parse it, and return a list of
    * route-objects.
    *
    * @param baseConfig The base-config containing the location of the route-config
    * @return           A list of Route objects
    */
   def loadRouteConfig(baseConfig: Config): List[Route] = {
      val loc = baseConfig.routeConfigurationLocation
      if (Files.exists(Paths.get(loc))) {
         try {
            val routeConfigContent = scala.io.Source.fromFile(Paths.get(loc).toUri).mkString
            JsonParser(routeConfigContent).convertTo[List[Route]]
         } catch {
            case ex: ParsingException =>
               log.warning(s"Could not parse router config: ${ex.getMessage}", ex)
               Nil
            case t: Throwable         =>
               log.warning(s"Could not load router config: ${t.getMessage}", t)
               Nil
         }
      } else {
         log.warning(s"No route file found at '$loc'")
         Nil
      }
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
            case ex: Throwable        => {
               log.warning(s"Could not load override config: ${ex.getMessage}")
            }
         }
      }

      config
   }


   /**
    * Returns the location of the override config if it exists (on the Filesystem)
    *
    * @param config The currently (default) config
    * @return       A path to the file if it exists and is readable, None otherwise
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


/**
 * A bunch of messages that the config load actor should be able to receive
 * and understand.
 */
object ConfigLoaderActor {

   case object ConfigLoaded

   case object ConfigLoadFailed

   case object LoadConfig

   case object ReLoadRoutes

}
