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
import com.aracon.greenscreen.service.{ HelloWorldService, StatusService, WebSocketService }
import com.librato.metrics.reporter.Librato
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.metrics._
import org.http4s.server.syntax._
import org.http4s.server.{ Router, Server, ServerApp }
import org.http4s.{ HttpService, Request, Response, Service }
import pureconfig._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig.error.ConfigReaderException

import scalaz.concurrent.Task

object Main extends ServerApp with Loggable {

  override def server(args: List[String]): Task[Server] =
    preStartOperations().fold(
      err => {
        error(err.getMessage, err)
        error("Errors during pre-Start phase. Program will now exit.")
        Task.fail(err)
      },
      config => {
        val rootService = configureAppServices(config)

        BlazeBuilder
          .bindHttp(config.port, config.interface)
          .enableHttp2(true)
          .withWebSockets(true)
          .withServiceExecutor(config.defaultPool)
          .mountService(rootService, config.appPrefix)
          .start
      }
    )

  private def configureAppServices(config: Config): HttpService = {
    val appServices: Service[Request, Response] =
    HelloWorldService.service(config) orElse StatusService.service(config) orElse WebSocketService.service

    // we want to disable metrics url in production so attackers can't access the data. We will get them via backend services like Librato
    val metricsRoute: List[(String, HttpService)] =
      if (config.isDev) ("/metrics" -> metricsService(config.metricRegistry)) :: Nil else Nil
    val routes
      : List[(String, HttpService)] = ("" -> Metrics(config.metricRegistry, "services")(appServices)) :: metricsRoute

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
        Librato
          .reporter(config.metricRegistry, config.librato.user, config.librato.token)
          .setSource(config.server.externalUrl)
          .start(10, TimeUnit.SECONDS)
      )
      .map(_ => info(s"Successfully configured Librato"))
  }

  private def runDBMigrations(config: Config): Either[Throwable, Unit] = {
    info("Starting database migrations with Flyway")

    FlywayMigration
      .startMigration(config.db)
      .toEither
      .map(n => info(s"Successfully applied $n migrations to the database"))
  }
}
