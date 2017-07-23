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
import com.aracon.greenscreen.model._
import cats.implicits._
import freestyle.cache.KeyValueMap
import fs2.interop.cats._
import org.scalacheck.Gen
import org.scalatest.Assertion

class SubmitAnswersSpec extends SpecTrait {

  //TODO: use eitherM instead of errorM for the program, to avoid exception! check in Gitter
  "returns Left if we can't load previous answers for the user" in fixture { _ =>
    val newAnswers = emptyAnswerMap
    assertThrows[UserAnswersNotFound] {
      run(submitAnswers[App.Op](1, newAnswers).interpret[TestStack])
    }
  }

  "returns the stored answers of the user if we could save the answers" in fixture { storage =>
    val aam1 = UserAnswers(1, Map("q1" -> Answer("q1", "a1"), "q2" -> Answer("q2", "a2")))
    storage.put(aam1.userId, aam1)

    val newAnswers: AnswerMap = Map("q3" -> Answer("q3", "a3"))
    val expected              = Map("q1" -> Answer("q1", "a1"), "q2" -> Answer("q2", "a2"), "q3" -> Answer("q3", "a3"))

    run(submitAnswers[App.Op](1, newAnswers).interpret[TestStack]) should be(expected)
  }

}
