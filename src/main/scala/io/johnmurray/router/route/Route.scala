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
                 destinationPath: String = matchPath)
