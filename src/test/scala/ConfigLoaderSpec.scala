import akka.actor.ActorSystem
import akka.testkit._

import io.johnmurray.router.config.ConfigLoaderActor

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
   }

}
