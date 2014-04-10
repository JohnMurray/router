import akka.actor.{Cancellable, ActorSystem}
import akka.testkit._

import io.johnmurray.router.config.{ConfigStore, ConfigLoaderActor}

import io.johnmurray.router.config.ConfigLoaderActor.{ReLoadRoutes, LoadConfig}
import io.johnmurray.router.route.Route
import org.specs2.mutable.Specification

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/3/14
 *
 * Specification for the config loader actor
 */
class ConfigLoaderSpec extends Specification {

   implicit val actorSystem = ActorSystem("config-loader-spec-system")

   sequential

   "config loader actor" should {

      val actor = TestActorRef(new ConfigLoaderActor).underlyingActor

      "loading base config" should {
         "load base configuration" in {
            actor.loadBaseConfig must not(throwA[Throwable])
         }

         "return None when override configuration is non-existent" in {
            sys.props += "overrideConfLocation" -> "/no/where/good.json"
            actor.getOverrideConfigLocation(actor.loadBaseConfig) must_== None
         }

         step { sys.props -= "overrideConfLocation" }

         "return Some(string) when override configuartion exists and is readable" in {
            val testLocation = getClass.getClassLoader.getResource("router.json").getFile
            sys.props += "overrideConfLocation" -> testLocation

            actor.getOverrideConfigLocation(actor.loadBaseConfig) must_== Some(testLocation)
         }

         step { sys.props -= "overrideConfLocation" }

         "return combined config if available" in {
            val testLocation = getClass.getClassLoader.getResource("router.json").getFile
            sys.props += "overrideConfLocation" -> testLocation

            val config = actor.loadBaseConfig
            config.port must_== 1
         }

         "set schedule for non-defined route-loading" in {
            actor.routeSchedule = None
            actor.receive(LoadConfig)
            actor.routeSchedule must beSome[Cancellable]
         }

         step { actor.routeSchedule = None }

         "not set schedule for defined route-loading" in {
            actor.receive(LoadConfig)
            val schedule = actor.routeSchedule
            actor.receive(LoadConfig)

            schedule must beSome[Cancellable]
            actor.routeSchedule must beSome[Cancellable]
            schedule must_== actor.routeSchedule
         }
      }

      "loading route config" should {

         val actor = TestActorRef(new ConfigLoaderActor).underlyingActor

         "load list of routes if route-file available" in {
            actor.receive(LoadConfig)
            ConfigStore.config = ConfigStore.config.copy(
               routeConfigurationLocation = getClass.getClassLoader.getResource("router-routes.json").getFile)

            actor.loadRouteConfig(ConfigStore.config) must have size 1
         }

         "return empty routes if route-file does not exist" in {
            actor.receive(LoadConfig)
            ConfigStore.config = ConfigStore.config.copy(
               routeConfigurationLocation = "/non/existent/router/file.json")

            actor.loadRouteConfig(ConfigStore.config) must_== Nil
         }

         "build route matcher in config-store if route-file available" in {
            actor.receive(LoadConfig)
            ConfigStore.config = ConfigStore.config.copy(
               routeConfigurationLocation = getClass.getClassLoader.getResource("router-routes.json").getFile)

            val routes = actor.loadRouteConfig(ConfigStore.config)
            actor.receive(ReLoadRoutes)

            ConfigStore.routeMatcher.routes must have size 1
            ConfigStore.routeMatcher.find(routes.head.matchPath) must beSome[Route]
         }
      }
   }

}
