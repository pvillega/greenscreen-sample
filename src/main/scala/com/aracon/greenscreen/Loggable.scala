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

import java.io.StringWriter
import java.io.PrintWriter

import com.github.nscala_time.time.Imports._
import org.fluentd.logger.scala.{ FluentLogger, FluentLoggerFactory }
import org.log4s.Logger

/*
 GCP uses Fluentd for logging purposes in GCE, see
 The format for the entries can be seen here:
  - for errors: https://cloud.google.com/error-reporting/docs/formatting-error-messages
 */
trait Loggable {
  protected val logger: Logger = org.log4s.getLogger

  protected val LOG: FluentLogger = FluentLoggerFactory.getLogger(this.getClass.getName)

  def info(msg: String) =
    logger.info(msg)

  def error(msg: String): Unit = {
    logger.error(msg)
    logData("errors", msg)
  }

  def error(msg: String, ex: Throwable): Unit = {
    val exceptionWriter = new StringWriter()
    ex.printStackTrace(new PrintWriter(exceptionWriter))
    error(s"$msg \n ${exceptionWriter.toString}")
  }

  //TODO: pass service context info from config, requires version to be part of our config data (Extracted from environment?)
  private def logData(label: String, msg: String): Unit =
    LOG.log(
      label,
      Map(
        "eventTime"      -> DateTime.now.toString,
        "message"        -> msg,
        "serviceContext" -> Map("service" -> "greenscreen", "version" -> "0.1"),
        "context" -> Map( // TODO: see https://cloud.google.com/error-reporting/reference/rest/v1beta1/ErrorContext we need better report for this
          "httpRequest" -> Map( // Data seen below may mean we need to send the error report only at the controler, when we have this context, and
            "method"             -> "GET",
            "url"                -> "requestUrl",
            "userAgent"          -> "UserAgent",
            "referrer"           -> "referrer",
            "responseStatusCode" -> "responseWeReturn",
            "remoteIp"           -> "remoteIp"
          ),
          "user" -> "singleUser",
          "reportLocation" -> Map("filePath" -> "Loggable.scala",
                                  "lineNumber"   -> "90",
                                  "functionName" -> "callerMethod")
        )
      )
    )
}
