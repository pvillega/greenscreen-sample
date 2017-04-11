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

package com.aracon.greenscreen.db.logger

import doobie.util.log._
import org.log4s.Logger

// Logger for Doobie queries, see https://tpolecat.github.io/doobie-cats-0.4.0/10-Logging.html
// Note: check version of Doobie we use if accessing the link above!
// Note: Doobie 0.4.0 doesn't support logging for 'process' and 'quick' constructs. It won't print anything
object LogbackDoobieLogger {
  // requires specific logger instead of using trait Loggable to be able to write to the file,
  protected val logger: Logger = org.log4s.getLogger

  implicit val logbackLogHandler: LogHandler = {
    LogHandler {

      case Success(s, a, e1, e2) =>
        val msg =
          s"""Successful Statement Execution:
             |
             | ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${a.mkString(", ")}]
             | elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
      """.stripMargin
        logger.info(msg)

      case ProcessingFailure(s, a, e1, e2, t) =>
        val msg =
          s"""Failed ResultSet Processing:
             |
             |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
             |
             | arguments = [${a.mkString(", ")}]
             | elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
             | failure = ${t.getMessage}
          """.stripMargin
        logger.error(msg)

      case ExecFailure(s, a, e1, t) =>
        val msg = s"""Failed Statement Execution:
        |
        | ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
        |
        | arguments = [${a.mkString(", ")}]
        | elapsed = ${e1.toMillis} ms exec (failed)
        | failure = ${t.getMessage}
      """.stripMargin
        logger.error(msg)
    }
  }
}
