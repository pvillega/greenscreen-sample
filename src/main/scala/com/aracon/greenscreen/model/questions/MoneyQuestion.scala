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

package com.aracon.greenscreen.model.questions

import cats.data.{ NonEmptyList, ValidatedNel }
import cats.implicits._
import com.aracon.greenscreen.model.types.Money
import com.aracon.greenscreen.model.{ Key, Question }

final case class MoneyQuestion(k: Key, question: String, systemQuestion: Boolean = false) extends Question[Money] {
  override def validate(t: Money): ValidatedNel[String, Unit] =
    ().validNel[String].ensuring(t.amount >= 0, NonEmptyList.of(s"$k: Amount can't be negative"))
}
