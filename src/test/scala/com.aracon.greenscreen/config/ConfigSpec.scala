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

import java.nio.file.Paths

import com.aracon.greenscreen._
import com.aracon.greenscreen.SpecTrait
import pureconfig.loadConfig
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._

class ConfigSpec extends SpecTrait {

  "Hardcoded configuration" - {
    val confFile = "application.conf"
    "is valid and can be loaded by pure config" - {
      // The following env vars are required as our config expects env vars to set the proper values, failing if they are not set.
      // This ensures the run environments are properly setup
      val requiredEnvVars = Map("LIBRATO_USER" -> "none", "LIBRATO_PASSWORD" -> "none", "LIBRATO_TOKEN" -> "none")
      requiredEnvVars.foreach { case (k, v) => System.setProperty(k, v) }

      s"Testing config for file $confFile" in {
        // we load files explicitly, to avoid System.setValue magic
        val resource = Thread.currentThread().getContextClassLoader.getResource(confFile)
        val path     = Paths.get(resource.toURI)

        loadConfig[Settings](path).fold(
          err => fail(s"Error loading configuration: $err."),
          _ => ()
        )
      }
    }
  }

}
