package io.johnmurray.router.route

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/5/14
 *
 * Represents the information that defines a route including what is matched against
 * and where the request should be routed to.
 */
case class Route(matchPath: String,
                 destinationHost: String,
                 destinationPort: Int = 80,
                 destinationPath: String) {

}


object Route {
   def apply(matchPath: String, destinationHost: String) =
      apply(matchPath = matchPath, destinationHost = destinationHost, destinationPath = matchPath)

   def apply(matchPath: String, destinationHost: String, destinationPort: Int) =
      apply(matchPath = matchPath, destinationHost = destinationHost, destinationPort = destinationPort, destinationPath = matchPath)
}
