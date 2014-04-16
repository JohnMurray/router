package io.johnmurray.router.handler

import akka.actor.Actor
import akka.event.Logging
import akka.io.IO

import io.johnmurray.router.config.ConfigStore

import spray.can.Http
import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.HttpHeaders.RawHeader

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

   override def postStop(): Unit = {
      IO(Http)(context.system) ! Http.Unbind
   }

   def receive = {
      case HttpRequest(method, Uri.Path(path: String), headers, entity, protocol) => {
         val route = ConfigStore.routeMatcher.find(path)

         log.info(s"Request: $method  $path  $protocol")
         log.info(s"Request: $entity")

         route match {
            case Some(r) => {
               log.info(s"route matched: $r")
               sender ! Http.Abort
            }
            case None    => {
               sender ! noRouteFound(path)
            }
         }
      }
      case Http.Connected(_, _)                                                   => {
         sender ! Http.Register(self)
      }
      case Http.PeerClosed                                                        => {
      }
      case Http.Bound(address)                                                    => {
         log.info(s"Bound at $address")
      }
      case Http.CommandFailed(failureMessage)                                     => {
         log.error(s"Failed to bind: $failureMessage")
      }
      case unknown                                                                => {
         log.warning(s"Unknown message received $unknown")
      }
   }


   def noRouteFound(path: String): HttpResponse = {
      HttpResponse(
         status = StatusCodes.NotFound,
         headers = List(RawHeader("X-Not-Found-Reason", s"Route '$path' not found"))
      )
   }

}
