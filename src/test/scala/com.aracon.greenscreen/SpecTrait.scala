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

package com.aracon.greenscreen

import cats._
import cats.data.Kleisli
import cats.implicits._
import com.aracon.greenscreen.config.{ Config, Settings }
import com.aracon.greenscreen.model.{ AnswerMap, Questionnaire, UserAnswers }
import com.aracon.greenscreen.algebras.all._
import com.aracon.greenscreen.modules.all._
import freestyle.cache.KeyValueMap
import freestyle.cache.hashmap._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{ FreeSpec, Matchers }
import pureconfig._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import fs2.Task

// To be inherited by all our tests, to provide common tooling and test interpreters
trait SpecTrait extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {

  // The following env vars are required as our config expects env vars to set the proper values, failing if they are not set.
  // This ensures the run environments are properly setup
  val requiredEnvVars = Map("LIBRATO_USER" -> "none", "LIBRATO_PASSWORD" -> "none", "LIBRATO_TOKEN" -> "none")
  requiredEnvVars.foreach { case (k, v) => System.setProperty(k, v) }

  val config: Config = loadConfig[Settings].bimap(l => throw new Exception(s"Failed to load config for tests: $l"), r => Config(r)).merge

  val questionnaire = Questionnaire()

  // we could use Id but that causes problems due to lack of MonadError instance for Id
  type TestStack[A] = Kleisli[Task, Config, A]

  implicit val cacheHandler: cacheP.CacheM.Handler[TestStack] = {
    val rawMap: KeyValueMap[Id, Long, UserAnswers] =
      new ConcurrentHashMapWrapper[Id, Long, UserAnswers]

    val cacheIdToStack: Id ~> TestStack =
      new (Id ~> TestStack) {
        def apply[A](a: Id[A]): TestStack[A] = Kleisli.lift(Task.now(a))
      }

    cacheP.implicits.cacheHandler(rawMap, cacheIdToStack)
  }

  private val mapStorage: KeyValueMap[Id, Long, UserAnswers] = new ConcurrentHashMapWrapper[Id, Long, UserAnswers]

  implicit val inMemoryStorageHandler: Storage.Handler[TestStack] = new Storage.Handler[TestStack] {

    def get(id: Long): TestStack[Option[UserAnswers]] =
      Kleisli.lift(Task.now(mapStorage.get(id)))

    def delete(id: Long): TestStack[Unit] =
      Kleisli.lift(Task.now(mapStorage.delete(id)))

    def save(id: Long, answerMap: AnswerMap): TestStack[Unit] =
      Kleisli.lift(Task.now(mapStorage.put(id, UserAnswers(id, answerMap))))

    def all: TestStack[List[UserAnswers]] =
      Kleisli.lift(Task.now(mapStorage.keys.flatMap(k => mapStorage.get(k).toList)))
  }

  def run[T](f: TestStack[T], config: Config = config): T = f.run(config).unsafeRun()

  def fixture(f: KeyValueMap[Id, Long, UserAnswers] => Any): Any = {
    mapStorage.clear

    f(mapStorage)
  }

}
