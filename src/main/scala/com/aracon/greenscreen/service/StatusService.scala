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

package com.aracon.greenscreen.service

import com.aracon.BuildInfo
import com.aracon.greenscreen.Loggable
import com.aracon.greenscreen.config.Config
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.joda.time.DateTime

object StatusService extends Loggable {

  def service(config: Config): HttpService =
    HttpService {
      case GET -> Root / "status" =>
        val status = Json.obj(
          ("timestamp", Json.fromString(DateTime.now().toString)),
          ("development", Json.fromBoolean(config.isDev)),
          ("name", Json.fromString(BuildInfo.name)),
          ("version", Json.fromString(BuildInfo.version))
        )
        val sensitiveInfo = Json.obj(
          ("scalaVersion", Json.fromString(BuildInfo.scalaVersion)),
          ("sbtVersion", Json.fromString(BuildInfo.sbtVersion)),
          ("externalUrl", Json.fromString(config.externalUrl)),
          ("prefix", Json.fromString(config.appPrefix)),
          ("bind-interface", Json.fromString(config.interface)),
          ("bind-port", Json.fromInt(config.port))
        )
        info(status.deepMerge(sensitiveInfo).spaces2)
        // only make available non-dangerous information
        Ok(status)
    }
}
