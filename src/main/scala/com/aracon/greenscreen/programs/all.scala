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

import com.aracon.greenscreen.algebras.all.{ UserAnswersNotFound, ValidationFailed }
import com.aracon.greenscreen.model.Questionnaire
import com.aracon.greenscreen.modules.all._
import com.aracon.greenscreen.model.{ AnswerMap, UserAnswers }
import com.aracon.greenscreen.model.poly._
import freestyle._
import freestyle.implicits._
import freestyle.logging._
import freestyle.effects._
import freestyle.implicits._
import freestyle.loggingJVM.implicits._
import cats.data.{ OptionT, ValidatedNel }
import cats.implicits._
import cats.syntax.either._

// Programs that define business logic of the application
object all {

  //TODO: add test interpreters and test the programs!!!

  //TODO: link to htp4s controllers
  //TODO: remove unused code from examples folders and similar!
  //TODO: turn errors like no user answer into 404 codes?

  def getAllUserAnswers[F[_]](implicit app: App[F]): FreeS[F, List[UserAnswers]] = {
    import app._, app.persistence._

    for {
      answersList <- storage.all
      _           <- log.info(s"Loaded all ${answersList.size} user answers from db.")
    } yield answersList
  }

  def getUserAnswers[F[_]](id: Long)(implicit app: App[F]): FreeS[F, Option[UserAnswers]] = {
    import app._, app.persistence._

    val getFromCache = for {
      cached <- cacheM.get(id).freeS
      _      <- log.info(s"Loaded from Cache answers for user $id : $cached")
    } yield cached

    val getFromDb = for {
      answersOpt <- storage.get(id).freeS
      _          <- log.info(s"Cache miss. Loaded from DB answers for user $id : $answersOpt")
      _          <- answersOpt.fold(().pure[FreeS[F, ?]])(ans => app.cacheM.put(id, ans))
      _          <- log.info(s"Added answers for user $id to cache")
    } yield answersOpt

    OptionT(getFromCache).orElseF(getFromDb).value
  }

  /* TODO validation is not compiling due to shapeless black magic...find why
  def validateAnswers[F[_], Q <: HList](
      questionnaire: Questionnaire[Q],
      answerMap: AnswerMap
  )(implicit app: App[F]): FreeS[F, ValidatedNel[String, Unit]] = {
    import app._

    val hListValidated: HList = new QuestionnaireValidation(answerMap)(questionnaire.questions)

    FreeS.pure(hListValidated.toList)
  }
   */

  //validates and saves if success. validation via default validation implemented, basic get(key).map() returns either[error, ok]
  def submitAnswers[F[_]](id: Long, userAnswers: AnswerMap)(implicit app: App[F]): FreeS[F, AnswerMap] = {
    import app._, app.errorM._, app.persistence._

    for {
      // TODO enable validation again once it works (see TODO above) and modify tests to include validation
      //      validation        <- validateAnswers[F, Q](questionnaire, userAnswers)
      //      _                 <- either(validation.toEither.leftMap(ValidationFailed))
      oldUserAnswersOpt <- getUserAnswers[F](id)
      oldUserAnswers    <- either(oldUserAnswersOpt.toRight(UserAnswersNotFound(id)))
      _                 <- log.info(s"Old user answers for user with id $id : $oldUserAnswers")
      newAnswers = oldUserAnswers.answersMap ++ userAnswers
      _ <- storage.save(id, newAnswers)
      _ <- log.info(s"Stored new answers for user with id $id : $newAnswers")
    } yield newAnswers
  }

}
