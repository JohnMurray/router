package io.johnmurray.router.config

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * The singleton (object) that holds the global config
 */
object ConfigStore {

   var _config : Option[Config] = None

   def config_=(config: Config) : Unit = this.synchronized {
      _config = Some(config)
   }

   def config : Config = this.synchronized { _config.get }
}
