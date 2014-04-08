import io.johnmurray.router.route.{Route, RouteMatcher}
import org.specs2.mutable.Specification

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/7/14
 *
 * Ensure that whatever mechanism we use for matching works. The underlying implementation
 * could be a Trie, it could be plain list of routes matched 1-by-1, it could be whatever.
 * We just need to ensure it meets our needs (currently little).
 */
class RouteMatcherSpec extends Specification {

   "Route Matcher" should {

      "basic things" should {
         "contain added path" in {
            val route = Route("/path", "localhost")
            val matcher = new RouteMatcher(Set(route))

            matcher.find("/path") must beSome[Route]
            matcher.find("/path").get must_== route
         }

         "return None when not found" in {
            val matcher = new RouteMatcher()

            matcher.find("/path") must beNone
         }
      }

      "matching for case sensitivity" should {
         "match case insensitive by deafult" in {
            val route = Route("/path", "localhost")
            val matcher = new RouteMatcher(Set(route))

            matcher.find("/PAth") must beSome[Route]
            matcher.find("/PAth").get must_== route
         }

         "match case sensitive when requested" in {
            val route = Route("/path", "localhost")
            val matcher = new RouteMatcher(Set(route), true)

            matcher.find("/PAth") must beNone
            matcher.find("/path") must beSome[Route]
            matcher.find("/path").get must_== route
         }

         "allow for same paths of mixed case when case-sensitive" in {
            val route1 = Route("/path", "localhost")
            val route2 = Route("/PAth", "other-host")
            val matcher = new RouteMatcher(Set(route1, route2), true)

            matcher.find("/path") must beSome[Route]
            matcher.find("/path").get must_== route1

            matcher.find("/PAth") must beSome
            matcher.find("/PAth").get must_== route2
         }

         "disallow for same paths of mixed case when case-insensitive" in {
            val route1 = Route("/path", "localhost")
            val route2 = Route("/PAth", "other-host")
            val matcher = new RouteMatcher(Set(route1, route2))

            matcher.find("/path").get must_== matcher.find("/PAth").get
         }
      }
   }

}
