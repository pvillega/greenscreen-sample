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

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.websocket._
import org.http4s.websocket.WebsocketBits._

import scala.concurrent.duration._
import scalaz.concurrent.{ Strategy, Task }
import scalaz.stream.async.unboundedQueue
import scalaz.stream.time.awakeEvery
import scalaz.stream.{ DefaultScheduler, Exchange, Process, Sink }

object WebSocketService {
  // current version uses scalaz-stream, this will need to be migrated to fs2 at some point
  def service: Service[Request, Response] =
    HttpService {
      case GET -> Root / "wsTest" =>
        Ok("WebServices endpoint active")

      case GET -> Root / "ws" =>
        val src = awakeEvery(1.seconds)(Strategy.DefaultStrategy, DefaultScheduler).map { d =>
          Text(s"Ping! $d")
        }
        val sink: Sink[Task, WebSocketFrame] = Process.constant {
          case Text(t, _) => Task.delay(println(t))
          case f          => Task.delay(println(s"Unknown type: $f"))
        }
        WS(Exchange(src, sink))

      case GET -> Root / "wsecho" =>
        val q = unboundedQueue[WebSocketFrame]
        val src = q.dequeue.collect {
          case Text(msg, _) => Text("You sent the server: " + msg)
        }

        WS(Exchange(src, q.enqueue))
    }
}
