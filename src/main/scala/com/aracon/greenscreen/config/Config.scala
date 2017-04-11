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

import java.nio.file.Path

import scala.util.Properties._
import java.util.concurrent.{ ExecutorService, Executors }
import javax.sql.DataSource

import com.codahale.metrics.MetricRegistry
import com.aracon.greenscreen._
import doobie.imports.{ DataSourceTransactor, IOLite }
import eu.timepit.refined.auto._
import org.h2.jdbcx.JdbcDataSource
import org.postgresql.jdbc3.Jdbc3SimpleDataSource

final case class DbConfig(driver: NonEmptyString, url: NonEmptyString, user: NonEmptyString, password: String)
final case class TlsKeyStore(enabled: Boolean, path: Path, password: NonEmptyString, managerPassword: NonEmptyString)
final case class Server(interface: NonEmptyString, port: ServerPort, prefix: String)
final case class Settings(server: Server, tlsKeyStore: TlsKeyStore, db: DbConfig)

final case class Config(settings: Settings) {
  // shortcuts for easy access to config values
  val server: Server           = settings.server
  val tlsKeyStore: TlsKeyStore = settings.tlsKeyStore
  val db: DbConfig             = settings.db

  val interface: String = server.interface
  val appPrefix: String = server.prefix
  val port: Int         = server.port

  // Due to issues with self-signed certs and secure webSockets we need to support non-tls options for development environment
  val httpProtocol: String = if (tlsKeyStore.enabled) "https" else "http"
  val wsProtocol: String   = if (tlsKeyStore.enabled) "wss" else "ws"

  // Working directory of the application
  val workPath: String = propOrEmpty("user.dir")

  // Default executor pool of the server. It may be a good idea to create a different pool for cpu-intensive tasks
  val defaultPool: ExecutorService = Executors.newCachedThreadPool()

  // Common metrics registry of the application. There should be only one per server
  val metricRegistry = new MetricRegistry()

  // unfortunately DataSource object don't have set methods. And setUrl differs in both implementations... ouch
  val datasource: DataSource = settings.db.driver.value match {
    case "h2" =>
      val ds = new JdbcDataSource()
      ds.setURL(settings.db.url)
      ds.setUser(settings.db.user)
      ds.setPassword(settings.db.password)
      ds
    case _ =>
      val ds = new Jdbc3SimpleDataSource()
      ds.setUrl(settings.db.url)
      ds.setUser(settings.db.user)
      ds.setPassword(settings.db.password)
      ds
  }

  // Currently uses IOLite monad for IO in Doobie, may be replaced later on for performance
  implicit val doobieTransactor = DataSourceTransactor[IOLite](datasource)
}