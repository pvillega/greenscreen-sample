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

package com.aracon.greenscreen.programs

import cats.Id
import com.aracon.greenscreen.SpecTrait
import com.aracon.greenscreen.programs.all._
import com.aracon.greenscreen.algebras.all._
import com.aracon.greenscreen.modules.all._
import freestyle._
import freestyle.implicits._
import freestyle.effects.error._
import freestyle.effects.error.implicits._
import freestyle.loggingJVM.implicits._
import cats.implicits._
import com.aracon.greenscreen.model._
import freestyle.cache.KeyValueMap
import fs2.interop.cats._
import org.scalatest.Assertion

class GetAllUserAnswersSpec extends SpecTrait {

  "returns empty list if there are no answers in the system" in fixture { _ =>
    run(getAllUserAnswers[App.Op].interpret[TestStack]) should be(Nil)
  }

  "returns all answers in the system if there are any" in fixture { storage =>
    val aam1 = UserAnswers(1, Map("q1"  -> Answer("q1", "a1"), "q2"   -> Answer("q2", "a2")))
    val aam2 = UserAnswers(2, Map("qw1" -> Answer("qw1", "a1"), "qw2" -> Answer("qw2", "a2")))

    storage.put(aam1.userId, aam1)
    storage.put(aam2.userId, aam2)
    val data = List(aam1, aam2)

    run(getAllUserAnswers[App.Op].interpret[TestStack]) should be(data)
  }

}
