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

package com.aracon.greenscreen.interpreters

import freestyle._
import cats.implicits._
import cats.data.State

object all {

//  type KVStoreState[A] = State[Map[String, Any], A]
//
//  implicit val doobieStorageHandler: Storage.Handler[KVStoreState] = new Storage.Handler[KVStoreState] {
//    def put[A](key: String, value: A): KVStoreState[Unit] =
//      State.modify(_.updated(key, value))
//    def get[A](key: String): KVStoreState[Option[A]] =
//      State.inspect(_.get(key).map(_.asInstanceOf[A]))
//    def delete(key: String): KVStoreState[Unit] =
//      State.modify(_ - key)
//  }

}
