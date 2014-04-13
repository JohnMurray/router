package io.johnmurray.router.route

import io.johnmurray.router.config.Route

import scala.collection.concurrent.TrieMap

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/7/14
 *
 * This class is responsible for matching a request-URI to a route-object in
 * its current collection of [[Route]] instances. Uses a Trie as the underlying
 * implementation for performant matching.
 *
 * Functionality Over Base Implementation
 *
 *   - Case Sensitivity can be turned on or off
 *
 * @param routes        The routes in which to match against
 * @param caseSensitive Flag to set case-sensitive (or not) matching
 */
case class RouteMatcher(routes : Set[Route] = Set.empty, caseSensitive: Boolean = false) {
   private val routeMap = new TrieMap[String, Route]()

   routes.map{ r =>
      if (caseSensitive) r
      else r.copy(matchPath = r.matchPath.toLowerCase)
   }.foreach { r =>
      routeMap += r.matchPath -> r
   }

   /**
    * Lookup a route given the path. Takes into account whether or not the match
    * should be done in a case-sensitive way or not
    *
    * @param path
    * @return
    */
   def find(path: String) : Option[Route] = {
      if (caseSensitive) {
         routeMap get path
      } else {
         routeMap get path.toLowerCase
      }
   }
}
