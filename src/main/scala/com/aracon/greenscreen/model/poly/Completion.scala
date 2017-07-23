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

package com.aracon.greenscreen.model.poly

import com.aracon.greenscreen.model._
import com.aracon.greenscreen.model.questions._
import shapeless.ops.hlist.Mapper
import shapeless.{ HList, Poly1 }

// compute completion
sealed trait Completion {
  def key: Key
}

case class Complete(key: Key)    extends Completion
case class NotComplete(key: Key) extends Completion

object CompletionCalculator {
  def calculate(q: Question[_], answerMap: AnswerMap): Completion =
    if (answerMap.contains(q.k)) Complete(q.k) else NotComplete(q.k)
}

class CompletionCalculator(answers: AnswerMap) {
  import CompletionCalculator._

  object Apply extends Poly1 {

    implicit def caseString = at[StringQuestion]((x: StringQuestion) => calculate(x, answers))

    implicit def caseNumeric = at[NumberQuestion]((x: NumberQuestion) => calculate(x, answers))

    implicit def caseBoolean = at[BooleanQuestion]((x: BooleanQuestion) => calculate(x, answers))

    implicit def caseAddress = at[AddressQuestion]((x: AddressQuestion) => calculate(x, answers))

    implicit def caseMoney = at[MoneyQuestion]((x: MoneyQuestion) => calculate(x, answers))

    implicit def caseDate = at[DateQuestion]((x: DateQuestion) => calculate(x, answers))

  }
  def apply[L <: HList](list: L)(implicit m: Mapper[Apply.type, L]): m.Out = list.map(Apply)
}
