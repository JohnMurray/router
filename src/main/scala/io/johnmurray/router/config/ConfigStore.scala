package io.johnmurray.router.config

import io.johnmurray.router.route.RouteMatcher

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * The singleton (object) that holds the global config
 *
 * TODO: really need to get rid of this somehow or another. I just hate the idea of
 *       global state and what not.
 */
object ConfigStore {

   private var _config : Option[Config] = None

   def config_=(config: Config) : Unit = {
      _config = Some(config)
   }

   /**
    * The config _should_ always be set. That being said, this will raise
    * and exception if no config is found. This means there is a dependency
    * at application start-up to ensure the config is loaded.
    *
    * @return Config object
    */
   def config : Config = _config.get



   private var _routeMatcher : RouteMatcher = RouteMatcher(Set.empty)

   def routeMatcher_=(routeMatcher: RouteMatcher) : Unit = {
      _routeMatcher = routeMatcher
   }

   def routeMatcher : RouteMatcher = _routeMatcher
}
