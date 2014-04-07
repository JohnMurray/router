package io.johnmurray.router.handler

import akka.actor.Actor
import akka.event.Logging
import akka.io.IO
import spray.can.Http

import spray.http.{HttpResponse, Uri, HttpRequest}
import spray.http.HttpMethods._
import io.johnmurray.router.config.ConfigStore

/**
 * Author: John Murray <jmurray@appnexus.com>
 * Date:   4/4/14
 *
 * Acts as the main handler for all HTTP requests.
 *
 * Currently this class only responds to "/ping" and responds w/ a "PONG"
 * message. This is for testing. This will change in the future when actual
 * proxy-support is added.
 */
class HttpHandler extends Actor {

   val log = Logging(context.system, this)

   override def preStart(): Unit = {
      IO(Http)(context.system) ! Http.Bind(self, interface = "localhost", port = ConfigStore.config.port)
   }

   override def postStop() : Unit = {
      IO(Http)(context.system) ! Http.Unbind
   }

   def receive = {
      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
         sender ! HttpResponse(entity = "PONG")

      case Http.Connected(_, _) =>
         sender ! Http.Register(self)

      case Http.PeerClosed =>

      case Http.Bound(address) =>
         log.info(s"Bound at $address")

      case Http.CommandFailed(failureMessage) =>
         log.error(s"Failed to bind: $failureMessage")

      case unknown =>
         log.warning(s"Unknown message received $unknown")
   }

}
