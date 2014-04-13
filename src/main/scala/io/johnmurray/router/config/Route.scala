package io.johnmurray.router.config

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/5/14
 *
 * Represents the information that defines a route including what is matched against
 * and where the request should be routed to.
 */
case class Route(matchPath: String,
                 destinationHost: String,
                 destinationPath: String,
                 destinationPort: Int = 80) {

   /**
    * Because we want to check equality based on the matchPath (to ensure
    * uniqueness based on route-paths). If we are able to define multiple
    * routes for the same match-path, it would be hard to do the matching
    * when the request came in because multiples would be returned. That
    * just doesn't make sense at the moment.
    *
    * @param that The other route to match against
    * @return
    */
   override def equals(that: Any) : Boolean = that match {
      case r: Route => this.matchPath == r.matchPath
      case _        => false
   }

}


object Route {
   def apply(matchPath: String, destinationHost: String) : Route =
      apply(matchPath, destinationHost, matchPath)

   def apply(matchPath: String, destinationHost: String, destinationPort: Int) : Route =
      apply(matchPath, destinationHost, matchPath, destinationPort)
}
