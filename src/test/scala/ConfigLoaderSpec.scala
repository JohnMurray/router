import akka.actor.{Cancellable, ActorSystem}
import akka.testkit._

import io.johnmurray.router.config.ConfigLoaderActor

import io.johnmurray.router.config.ConfigLoaderActor.LoadConfig
import org.specs2.mutable.Specification

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/3/14
 *
 * Specification for the config loader actor
 */
class ConfigLoaderSpec extends Specification {

   implicit val actorSystem = ActorSystem("config-loader-spec-system")
   val actorRef = TestActorRef(new ConfigLoaderActor)
   val actor = actorRef.underlyingActor

   "config loader actor" should {

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

         "not set schedule for defined route-loading" in {
            actor.routeSchedule = None

            actor.receive(LoadConfig)
            val schedule = actor.routeSchedule
            actor.receive(LoadConfig)

            schedule must beSome[Cancellable]
            actor.routeSchedule must beSome[Cancellable]
            schedule must_== actor.routeSchedule
         }
      }

      "loading route config" should {
         "placeholder" in {
            1 must_== 1
         }
      }
   }

}
