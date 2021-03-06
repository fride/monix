/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
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

package monix.types

/** The `Applicative` type-class is a [[Functor]] that also adds the
  * capability of lifting a value in the context.
  *
  * Described in
  * [[http://www.soi.city.ac.uk/~ross/papers/Applicative.html
  * Applicative Programming with Effects]].
  *
  * The purpose of this type-class is to support the data-types in the
  * Monix library and it is considered a shim for a lawful type-class
  * to be supplied by libraries such as Cats or Scalaz or equivalent.
  *
  * To implement it in instances, inherit from [[ApplicativeClass]].
  *
  * Credit should be given where it is due. The type-class encoding has
  * been copied from the Scado project and
  * [[https://github.com/scalaz/scalaz/ Scalaz 8]] and the type has
  * been extracted from [[http://typelevel.org/cats/ Cats]].
  */
trait Applicative[F[_]] extends Serializable {
  def functor: Functor[F]

  def pure[A](a: A): F[A]
  def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) => Z): F[Z]
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
}

object Applicative extends ApplicativeSyntax {
  @inline def apply[F[_]](implicit F: Applicative[F]): Applicative[F] = F
}

/** The `ApplicativeClass` provides the means to combine
  * [[Applicative]] instances with other type-classes.
  *
  * To be inherited by `Applicative` instances.
  */
trait ApplicativeClass[F[_]] extends Applicative[F] with FunctorClass[F] {
  final def applicative: Applicative[F] = this
}

/** Provides syntax for [[Applicative]]. */
trait ApplicativeSyntax extends Serializable {
  implicit final def applicativeOps[F[_], A, B](ff: F[A => B])
    (implicit F: Applicative[F]): ApplicativeSyntax.OpsAP[F, A, B] =
    new ApplicativeSyntax.OpsAP(ff)
}

object ApplicativeSyntax {
  final class OpsAP[F[_], A, B](self: F[A => B])(implicit F: Applicative[F])
    extends Serializable {

    /** Extension method for [[Applicative.ap]]. */
    def ap(fa: F[A]): F[B] = F.ap(self)(fa)
  }
}
