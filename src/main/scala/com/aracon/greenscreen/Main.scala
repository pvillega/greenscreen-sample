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

package com.aracon.greenscreen

import java.util.concurrent.TimeUnit

import cats.implicits._
import com.aracon.greenscreen.config.{ Config, Settings }
import com.aracon.greenscreen.db.migration.FlywayMigration
import com.aracon.greenscreen.service.example.{ HelloWorldService, WebSocketService }
import com.aracon.greenscreen.service.StatusService
import com.librato.metrics.reporter.Librato
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.metrics._
import org.http4s.server.syntax._
import org.http4s.server.Router
import org.http4s._
import pureconfig._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import fs2.{ Stream, Task }
import org.http4s.util.StreamApp
import pureconfig.error.ConfigReaderException

object Main extends StreamApp with Loggable {

  override def stream(args: List[String]): Stream[Task, Nothing] =
    preStartOperations().fold(
      err => {
        error(err.getMessage, err)
        error("Errors during pre-Start phase. Program will now exit.")
        Stream.eval(Task.fail(err))
      },
      config => {
        val rootService = configureAppServices(config)

        BlazeBuilder
          .bindHttp(config.port, config.interface)
          .enableHttp2(true)
          .withWebSockets(true)
          .withServiceExecutor(config.defaultPool)
          .mountService(rootService, config.appPrefix)
          .serve
      }
    )

  private def configureAppServices(config: Config): HttpService = {
    val appServices: HttpService =
    HelloWorldService.service(config) orElse StatusService.service(config) orElse WebSocketService.service

    // we want to disable metrics url in production so attackers can't access the data. We will get them via backend services like Librato
    val metricsRoute = if (config.isDev) ("/metrics" -> metricsService(config.metricRegistry)) :: Nil else Nil
    val routes       = ("" -> Metrics(config.metricRegistry, "services")(appServices)) :: metricsRoute

    Router(routes: _*)
  }

  private def preStartOperations(): Either[Throwable, Config] =
    for {
      settings <- loadAppConfiguration()
      conf = Config(settings)
      _ <- initialiseLibrato(conf)
      _ <- runDBMigrations(conf)
    } yield conf

  private def loadAppConfiguration(): Either[Throwable, Settings] = {
    info("Initialising the config object")

    loadConfig[Settings]
      .leftMap(ConfigReaderException(_))
  }

  private def initialiseLibrato(config: Config): Either[Throwable, Unit] = {
    info("Linking metrics to Librato provider")

    Either
      .catchNonFatal(
        if (config.isDev) {
          warn(s"Skipping Librato in DEV environments")
        } else {
          Librato
            .reporter(config.metricRegistry, config.librato.user, config.librato.token)
            .setSource(config.server.externalUrl)
            .start(10, TimeUnit.SECONDS)

          info(s"Successfully configured Librato")
        }
      )
  }

  private def runDBMigrations(config: Config): Either[Throwable, Unit] = {
    info("Starting database migrations with Flyway")

    FlywayMigration
      .startMigration(config.db)
      .toEither
      .map(n => info(s"Successfully applied $n migrations to the database"))
  }
}
