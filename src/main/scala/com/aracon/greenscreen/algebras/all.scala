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

package com.aracon.greenscreen.algebras

import freestyle._
import freestyle.implicits._
import cats.data.NonEmptyList
import cats.implicits._
import com.aracon.greenscreen.model.{ AnswerMap, UserAnswers }

object all {

  sealed abstract class APIException(val msg: String)             extends Exception(msg)
  final case class UserAnswersNotFound(id: Long)                  extends APIException(s"UserAnswers for user $id can't be found")
  final case class DeletionError(id: Long)                        extends APIException(s"UserAnswers with for user $id can't be deleted")
  final case class ValidationFailed(errors: NonEmptyList[String]) extends APIException(errors.show)

  @free
  trait Storage {
    def get(id: Long): FS[Option[UserAnswers]]
    def delete(id: Long): FS[Unit]
    def save(id: Long, answerMap: AnswerMap): FS[Unit]
    def all: FS[List[UserAnswers]]
  }

}
