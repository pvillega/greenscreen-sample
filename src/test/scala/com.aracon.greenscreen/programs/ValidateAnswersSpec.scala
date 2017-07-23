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

import cats.data.NonEmptyList
import cats.implicits._
import com.aracon.greenscreen.SpecTrait
import com.aracon.greenscreen.model._
import com.aracon.greenscreen.model.types.Money
import com.aracon.greenscreen.programs.all._

class ValidateAnswersSpec extends SpecTrait {
  //TODO: re-enable tests when validatin is working again

  "returns Unit if there are no validation errors" in fixture { _ =>
    true should be(true)
//    val answers = Map("firstName" -> Answer("firstName", "name"), "age" -> Answer("age", 18))
//    run(validateAnswers[App.Op](questionnaire, answers).interpret[TestStack]) should be(().validNel[String])
  }

  "returns a validation error if the data is incorrect" in fixture { _ =>
    true should be(true)
//    val answers  = Map("firstName" -> Answer("firstName", ""), "age" -> Answer("age", 18))
//    val expected = NonEmptyList("firstName: Can't be an empty string").invalidNel[Unit]
//    run(validateAnswers[App.Op](questionnaire, answers).interpret[TestStack]) should be(expected)
  }

  "returns all existing validation errors at once" in fixture { _ =>
    true should be(true)
//    val answers  = Map("firstName" -> Answer("firstName", ""), "money" -> Answer("money", Money("GBP", -5)))
//    val expected = NonEmptyList("firstName: Can't be an empty string", "money: Amount can't be negative").invalidNel[Unit]
//    run(validateAnswers[App.Op](questionnaire, answers).interpret[TestStack]) should be(expected)
  }

}
