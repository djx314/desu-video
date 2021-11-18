package desu.number

import cats._
import cats.implicits._
import cats.effect._
import cats.effect.kernel.MonadCancel

trait NFlatMap[F[_], A] {
  def f[T](fun: A => F[T]): F[T]
}

trait Number1[Context[_], T] {
  def run(number2: Number2): Context[T]
}
case class Number1S[Context[_], T, U](tail: U => Number1[Context, T], flatMap: NFlatMap[Context, U]) extends Number1[Context, T] {
  def run(number2: Number2): Context[T] = flatMap.f(u => tail(u).run(number2))
}
case class Number1T[Context[_], T](head: Context[T]) extends Number1[Context, T] {
  def run(number2: Number2): Context[T] = head
}

trait Number2
case object Number2S extends Number2

class NContext[F[_]] {
  trait Number1FlatMap[F[_], U] {
    def flatMap[T](u: U => Number1[F, T]): Number1[F, T]
  }
  trait Number1Map[F[_], U] {
    def map[T](u: U => T): Number1[F, T]
  }

  val runner: Number2 = Number2S

  def flatMap[U](a: F[U])(implicit i: FlatMap[F]): Number1FlatMap[F, U] = new Number1FlatMap[F, U] {
    override def flatMap[T](u: U => Number1[F, T]): Number1[F, T] = Number1S(
      u,
      new NFlatMap[F, U] {
        override def f[T](fun: U => F[T]): F[T] = a.flatMap(fun)
      }
    )
  }
  def map[U](a: F[U])(implicit i1: Functor[F]): Number1Map[F, U] = new Number1Map[F, U] {
    override def map[T](fun: U => T): Number1[F, T] = Number1T(Functor[F].map(a)(fun))
  }
  def resource_use[F[_], U](a: Resource[F, U])(implicit v: MonadCancel[F, Throwable]): Number1FlatMap[F, U] = new Number1FlatMap[F, U] {
    override def flatMap[T](u: U => Number1[F, T]): Number1[F, T] = Number1S(
      u,
      new NFlatMap[F, U] {
        override def f[T](fun: U => F[T]): F[T] = a.use(fun)
      }
    )
  }
}
