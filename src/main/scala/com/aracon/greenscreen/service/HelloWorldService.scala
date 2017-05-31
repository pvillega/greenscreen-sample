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

import java.util.concurrent.TimeUnit

import com.aracon.greenscreen.Loggable
import com.aracon.greenscreen.config.Config
import com.aracon.greenscreen.db.DBQueries
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.refined._
import eu.timepit.refined.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.twirl._

object HelloWorldService extends Loggable {
  def service(config: Config): Service[Request, Response] = {
    val rootCount   = config.metricRegistry.counter("root-access")
    val testDbTimer = config.metricRegistry.timer("test-db-time")

    HttpService {
      case GET -> Root =>
        rootCount.inc()
        // Supports Play Framework template -- see src/main/twirl.
        Ok(html.index(s"${config.wsBaseUrl}/ws"))

      case GET -> Root / "test" =>
        val start = System.nanoTime()
        val tests = DBQueries.getAllTests(config.doobieTransactor).unsafePerformIO
        testDbTimer.update(System.nanoTime() - start, TimeUnit.NANOSECONDS)

        Ok(tests.asJson)

      case GET -> Root / "flags" =>
        Ok(config.flags.asJson)
    }
  }
}
