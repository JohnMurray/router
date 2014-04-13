import io.johnmurray.router.config.Route
import org.specs2.mutable.Specification

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/7/14
 *
 * Just ensure that we can verify uniqueness of a [[Route]] object based on the
 * path specified within the route.
 */
class RouteSpec extends Specification {

   "route" should {
      "check uniqueness based on route-path" in {
         val route1 = Route("/path", "localhost")
         val route2 = Route("/path", "remote-host")

         Set(route1, route2).size must_== 1

         (Set(route1) + route2).head must_== route1
      }

      "check that different paths with different cases are accounted for" in {
         val route1 = Route("/path", "localhost")
         val route2 = Route("/PATH", "other-host")

         route1 must_!= route2
      }

   }
}
