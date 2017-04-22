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

package com.aracon.greenscreen.db.migration

import com.aracon.greenscreen.config.DbConfig
import org.flywaydb.core.Flyway
import eu.timepit.refined.auto._
import scala.util.Try

object FlywayMigration {

  // returns number of migrations applied
  def startMigration(config: DbConfig): Try[Int] = Try {
    val flyway = new Flyway()
    flyway.setDataSource(config.url, config.user, config.password)
    flyway.migrate()
  }
}
