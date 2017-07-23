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

package com.aracon.greenscreen.modules

import cats.data.ValidatedNel
import freestyle._
import freestyle.cache._
import freestyle.effects._
import freestyle.effects.error._
import freestyle.logging.LoggingM
import freestyle.implicits._
import freestyle.loggingJVM.implicits._
import freestyle.effects.error.implicits._
import cats.implicits._
import com.aracon.greenscreen.algebras.all._
import com.aracon.greenscreen.config.Config
import com.aracon.greenscreen.model.UserAnswers
import freestyle.effects.error.ErrorM

object all {
//  val rd     = reader[Config]
  val cacheP = new KeyValueProvider[Long, UserAnswers]

  @module
  trait Persistence {
    val storage: Storage
  }

  @module
  trait App {
    val persistence: Persistence

    val errorM: ErrorM
    val cacheM: cacheP.CacheM
//    val readerM: rd.ReaderM
    val log: LoggingM
  }

}
