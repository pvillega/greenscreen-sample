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

package com.aracon.greenscreen.db

import com.aracon.greenscreen._
import com.aracon.greenscreen.db.logger.LogbackDoobieLogger
import com.aracon.greenscreen.model.Test
import doobie.imports._
import doobie.util.log.LogHandler

object DBQueries {
  implicit val doobieLog: LogHandler = LogbackDoobieLogger.logbackLogHandler

  def getAllTests[M[_]](xa: Transactor[M]) =
    sql"SELECT ID, NAME FROM Test"
      .query[Test]
      .list
      .transact(xa)
}
