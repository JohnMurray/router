package io.johnmurray.router.config

import spray.json._
import DefaultJsonProtocol._

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/26/14
 *
 * Contains the "protocol" objects for (de)serialization of Scala
 * 'config' objects.
 */
object RouterJsonProtocols {

   implicit val configFormat = jsonFormat2(Config)

}
