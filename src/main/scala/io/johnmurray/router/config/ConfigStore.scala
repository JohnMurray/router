package io.johnmurray.router.config

import io.johnmurray.router.route.Route

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * The singleton (object) that holds the global config
 */
object ConfigStore {

   var _config : Option[Config] = None

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



   var _routes : List[Route] = Nil

   def routes_=(routes: List[Route]) : Unit = {
      _routes = routes
   }

   def routes : List[Route] = _routes
}
