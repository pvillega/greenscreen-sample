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
import shapeless.{ HList, Poly1 }
import shapeless.ops.hlist.Mapper

// render as NodeSeq using a polymorphic function
trait HtmlRenderer extends Poly1 {
  implicit def caseString = at[StringQuestion]((x: StringQuestion) => <span>{x.question}:</span> ++ <input id={x.k}/>)

  implicit def caseNumeric = at[NumberQuestion]((x: NumberQuestion) => <span>{x.question}:</span> ++ <input id={x.k}/>)

  implicit def caseBoolean =
    at[BooleanQuestion]((x: BooleanQuestion) => <span>{x.question}:</span> ++ <checkbox id={x.k}/>)

  implicit def caseAddress =
    at[AddressQuestion](
      (x: AddressQuestion) =>
        <span>{x.question}:</span> ++ <input id={s"${x.k}-line1"}/> ++ <input id={s"${x.k}-line2"}/> ++ <input id={s"${x.k}-postCode"}/> ++ <input id={s"${x.k}-country"}/>
    )

  implicit def caseMoney =
    at[MoneyQuestion](
      (x: MoneyQuestion) =>
        <span>{x.question}:</span> ++ <input id={s"${x.k}-currency"}/> ++ <input id={s"${x.k}-amount"}/>
    )
}

object HtmlRenderer extends HtmlRenderer {
  // cheeky putting this here but it makes it easier to use
  def apply[L <: HList](list: L)(implicit m: Mapper[HtmlRenderer.type, L]): m.Out = list.map(HtmlRenderer)
}
