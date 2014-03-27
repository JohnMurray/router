package io.johnmurray.router

import akka.actor.{Props, ActorSystem}
import akka.pattern.Patterns

import io.johnmurray.router.config.ConfigLoaderActor

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   3/25/14
 *
 * Entry point for application
 */
object Boot extends App {

   implicit val actorSystem = ActorSystem("router")

   val configLoader = actorSystem.actorOf(Props[ConfigLoaderActor])

   /*
    * Load the config and set the config-loader-actor's schedule to start running
    */
   val configLoadResult = Await.result(
      Patterns.ask(configLoader, (ConfigLoaderActor.LoadConfig, true), 500.milliseconds),
      500.milliseconds)
   if (configLoadResult == ConfigLoaderActor.ConfigLoadFailed) {
      println("Could not load the configuration")
      sys.exit(1)
   }
   actorSystem.scheduler.schedule(10.seconds, 10.seconds, configLoader, ConfigLoaderActor.LoadConfig)

}
