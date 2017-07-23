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

import cats.data.ValidatedNel
import cats.implicits._
import com.aracon.greenscreen.model._
import com.aracon.greenscreen.model.questions._
import shapeless.ops.hlist.Mapper
import shapeless.{ HList, Poly1 }

class QuestionnaireValidation(answers: AnswerMap) {

  object QuestionValidation extends Poly1 {

    implicit def caseString = at[StringQuestion]((x: StringQuestion) => validate(x, answers))

    implicit def caseNumeric = at[NumberQuestion]((x: NumberQuestion) => validate(x, answers))

    implicit def caseBoolean = at[BooleanQuestion]((x: BooleanQuestion) => validate(x, answers))

    implicit def caseAddress = at[AddressQuestion]((x: AddressQuestion) => validate(x, answers))

    implicit def caseMoney = at[MoneyQuestion]((x: MoneyQuestion) => validate(x, answers))

    implicit def caseDate = at[DateQuestion]((x: DateQuestion) => validate(x, answers))

    private def validate[T](q: Question[T], answerMap: AnswerMap): ValidatedNel[String, Unit] =
      answerMap.get(q.k).fold(().validNel[String])(a => q.validate(a.v.asInstanceOf[T]))

  }

  def apply[L <: HList](list: L)(implicit m: Mapper[QuestionValidation.type, L]): m.Out = list.map(QuestionValidation)
}
