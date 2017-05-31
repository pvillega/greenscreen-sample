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

package com.aracon.greenscreen.config

import java.util.concurrent.{ ExecutorService, Executors }

import com.aracon.greenscreen._
import com.codahale.metrics.MetricRegistry
import doobie.imports.{ IOLite, Transactor }
import doobie.util.transactor.DriverManagerTransactor
import eu.timepit.refined.auto._
import cron4s.expr.CronExpr

import scala.util.Properties._

final case class DbConfig(driver: NonEmptyString, url: NonEmptyString, user: NonEmptyString, password: String)
final case class Server(externalUrl: NonEmptyString, interface: NonEmptyString, port: ServerPort, prefix: String)
final case class LibratoConfig(user: NonEmptyString, password: NonEmptyString, token: NonEmptyString)
final case class Flags(isDev: Boolean, switchThisOn: Boolean)
final case class Scheduler(frequency: CronExpr)
final case class Settings(flags: Flags, server: Server, db: DbConfig, librato: LibratoConfig, scheduler: Scheduler)

final case class Config(settings: Settings) {
  // shortcuts for easy access to config values
  val flags: Flags           = settings.flags
  val server: Server         = settings.server
  val db: DbConfig           = settings.db
  val scheduler: Scheduler   = settings.scheduler
  val librato: LibratoConfig = settings.librato

  val isDev: Boolean = flags.isDev

  val externalUrl: String = server.externalUrl
  val interface: String   = server.interface
  val port: Int           = server.port
  val appPrefix: String   = server.prefix
  val serverPath: String  = s"$externalUrl$appPrefix"

  // Due to issues with self-signed certs and secure webSockets we need to support non-tls options for development environment
  val httpProtocol: String = if (isDev) "http" else "https"
  val httpBaseUrl: String  = s"$httpProtocol://$serverPath"
  val wsProtocol: String   = if (isDev) "ws" else "wss"
  val wsBaseUrl: String    = s"$wsProtocol://$serverPath"

  // Working directory of the application
  val workPath: String = propOrEmpty("user.dir")

  // Default executor pool of the server. It may be a good idea to create a different pool for cpu-intensive tasks
  val defaultPool: ExecutorService = Executors.newCachedThreadPool()

  // Common metrics registry of the application. There should be only one per server.
  val metricRegistry = new MetricRegistry()

  // Currently uses IOLite monad for IO in Doobie, may be replaced later on for performance
  implicit val doobieTransactor: Transactor[IOLite] =
    DriverManagerTransactor[IOLite](settings.db.driver, settings.db.url, settings.db.user, settings.db.password)
}
