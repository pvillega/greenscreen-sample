/*
 * Copyright 2017 Pere Villega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aracon.greenscreen.service.example

import fs2._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.websocket._
import org.http4s.websocket.WebsocketBits._

import scala.concurrent.duration._

object WebSocketService {
  implicit val scheduler: Scheduler = Scheduler.fromFixedDaemonPool(2)
  implicit val strategy: Strategy   = Strategy.fromFixedDaemonPool(8, threadName = "worker")

  // An infinite stream of the periodic elapsed time
  val seconds: Stream[Task, FiniteDuration] = time.awakeEvery[Task](1.second)

  def service: HttpService =
    HttpService {
      case GET -> Root / "wsTest" =>
        Ok("WebServices endpoint active")

      case GET -> Root / "ws" =>
        val fromClient: Sink[Task, WebSocketFrame] = _.evalMap { (ws: WebSocketFrame) =>
          ws match {
            case Text(t, _) => Task.delay(println(t))
            case f          => Task.delay(println(s"Unknown type: $f"))
          }
        }
        val toClient: Stream[Task, WebSocketFrame] = seconds.map { d =>
          Text(s"Ping! ${d.toMillis}")
        }
        WS(toClient, fromClient)

      case GET -> Root / "wsecho" =>
        val queue = async.unboundedQueue[Task, WebSocketFrame]
        val echoReply: Pipe[Task, WebSocketFrame, WebSocketFrame] = pipe.collect {
          case Text(msg, _) => Text("You sent the server: " + msg)
          case _            => Text("Something new")
        }

        queue.flatMap { q =>
          val d = q.dequeue.through(echoReply)
          val e = q.enqueue
          WS(d, e)
        }
    }

}
