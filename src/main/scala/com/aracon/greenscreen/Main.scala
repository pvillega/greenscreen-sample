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

import cats.implicits._
import com.aracon.greenscreen.config.{ Config, Settings }
import com.aracon.greenscreen.db.migration.FlywayMigration
import com.aracon.greenscreen.service.{ HelloWorldService, WebSocketService }
import org.http4s.server.SSLSupport.StoreInfo
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.metrics._
import org.http4s.server.syntax._
import org.http4s.server.{ Router, Server, ServerApp }
import org.http4s.{ HttpService, Request, Response, Service }
import pureconfig._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import scalaz.concurrent.Task

object Main extends ServerApp with Loggable {

  override def server(args: List[String]): Task[Server] =
    preStartOperations().fold(
      err => {
        error(err)
        error("Errors during pre-Start phase. Program will now exit.")
        Task.fail(new RuntimeException(err))
      },
      config => {
        val rootService = configureAppServices(config)

        builder(config)
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
    HelloWorldService.service(config) orElse WebSocketService.service(config)

    Router(
      ""         -> Metrics(config.metricRegistry, "services")(appServices),
      "/metrics" -> metricsService(config.metricRegistry)
    )
  }

  // Due to some webSocket issues with local development, we need to disable TLS during development
  private def builder(config: Config): BlazeBuilder =
    if (config.tlsKeyStore.enabled) {
      BlazeBuilder.withSSL(StoreInfo(config.tlsKeyStore.path.toString, config.tlsKeyStore.password),
                           keyManagerPassword = config.tlsKeyStore.managerPassword)
    } else {
      BlazeBuilder
    }

  private def preStartOperations(): Either[String, Config] =
    for {
      settings <- loadAppConfiguration()
      conf = Config(settings)
      _ <- runDBMigrations(conf)
    } yield conf

  private def loadAppConfiguration(): Either[String, Settings] = {
    info(s"Current working folder: ${System.getProperty("user.dir")}")
    info("Initialising the config object")
    loadConfig[Settings]
      .leftMap(err => s"Error loading configuration: $err")
  }

  private def runDBMigrations(config: Config): Either[String, Unit] = {
    info("Starting database migrations with Flyway")

    FlywayMigration
      .startMigration(config.db)
      .toEither
      .map(n => info(s"Successfully applied $n migrations to the database with url ${config.db.url}"))
      .leftMap { ex =>
        s"Error while applying migrations to the database with url ${config.settings.db.url}: ${ex.getMessage}\n ${ex.getStackTrace
          .mkString("\n")}"
      }
  }
}
