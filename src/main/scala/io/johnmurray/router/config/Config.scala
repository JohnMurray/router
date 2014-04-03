package io.johnmurray.router.config

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * Represents all configuration for the project
 */
case class Config(port: Int,
                  matcherConfigurationLocation: String,
                  overrideConfigurationLocation: String) {

   def merge(other: OverrideConfig) : Config = {
      copy(port = other.port.getOrElse(port),
           matcherConfigurationLocation = other.matcherConfigurationLocation.getOrElse(matcherConfigurationLocation)
   }
}

/**
 * Represents the optional "override" configurations that can be given by
 * an external config
 */
case class OverrideConfig(port: Option[Int],
                          matcherConfigurationLocation: Option[String])

