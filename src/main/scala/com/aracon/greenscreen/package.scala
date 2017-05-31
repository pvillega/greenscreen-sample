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

package com.aracon

import cron4s.Cron
import cron4s.expr.CronExpr
import doobie.imports.{ Composite, Meta }
import doobie.util.invariant.InvalidObjectMapping
import eu.timepit.refined.W
import eu.timepit.refined.api.{ RefType, Refined, Validate }
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{ Greater, Positive }
import pureconfig._
import pureconfig.ConvertHelpers._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

package object greenscreen {

  type Traversable[+A] = scala.collection.immutable.Traversable[A]
  type Iterable[+A]    = scala.collection.immutable.Iterable[A]
  type Seq[+A]         = scala.collection.immutable.Seq[A]
  type IndexedSeq[+A]  = scala.collection.immutable.IndexedSeq[A]

  type PositiveInt    = Int Refined Positive
  type NonEmptyString = String Refined NonEmpty
  type ServerPort     = Int Refined Greater[W.`1024`.T]

  // PureConfig 0.6.0+: Force PureConfig to expect .conf keys as CamelCase instead of kebab-case-key
  implicit def productHint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  // ConfigReader to parse Cron expressions via PureConfig
  implicit val cronConfigReader: ConfigReader[CronExpr] = ConfigReader.fromString[CronExpr](tryF(Cron.tryParse))

  // Fixed on Doobie 0.4.2? Allows generation of doobie Meta objects from Refined types
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit def refinedMeta[T: Meta, P, F[_, _]](implicit tt: TypeTag[F[T, P]],
                                                ct: ClassTag[F[T, P]],
                                                validate: Validate[T, P],
                                                refType: RefType[F]): Meta[F[T, P]] =
    Meta[T].xmap(refType.refine[P](_) match {
      case Left(_)  => throw InvalidObjectMapping(ct.runtimeClass, ct.getClass)
      case Right(t) => t
    }, refType.unwrap)

  // Fixed on Doobie 0.4.2? Allows generation of doobie Composite objects from Refined types
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit def refinedComposite[T: Composite, P, F[_, _]](implicit ct: ClassTag[F[T, P]],
                                                          validate: Validate[T, P],
                                                          refType: RefType[F]): Composite[F[T, P]] =
    Composite[T].imap(refType.refine[P](_) match {
      case Left(_)  => throw InvalidObjectMapping(ct.runtimeClass, ct.getClass)
      case Right(t) => t
    })(refType.unwrap)
}
