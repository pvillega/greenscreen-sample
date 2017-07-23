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

package com.aracon.greenscreen.model

import com.aracon.greenscreen.model.questions._
import shapeless.{ HList, HNil }

final case class Questionnaire[T <: HList](questions: T)

object Questionnaire {

  val creationDate = DateQuestion("creationDate", "Creation Date", systemQuestion = true)
  val firstName    = StringQuestion("firstName", "First Name")
  val lastName     = StringQuestion("lastName", "Last Name")
  val age          = NumberQuestion("age", "Your age")
  val address      = AddressQuestion("address", "Address")
  val deposit      = MoneyQuestion("deposit", "Amount to deposit")
  val tAndC        = BooleanQuestion("tAndC", "I agree with T & C")

  private val questions = creationDate :: firstName :: lastName :: age :: address :: deposit :: tAndC :: HNil

  def apply() = new Questionnaire(questions)
}
