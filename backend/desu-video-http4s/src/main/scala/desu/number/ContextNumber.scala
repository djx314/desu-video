package desu.number

import cats._
import cats.implicits._
import cats.effect._
import cats.effect.kernel.MonadCancel

trait Number1[Context[_], T] {
  def method1(number2: Number2): Context[T]
}
case class Number1S[Context[_]: FlatMap, T, U](tail: U => Number1[Context, T], head: Context[U]) extends Number1[Context, T] {
  def method1(number2: Number2): Context[T] = head.flatMap(s => number2.method2(tail(s)))
}
case class Number1T[Context[_]: Sync, T](head: () => T) extends Number1[Context, T] {
  def method1(number2: Number2): Context[T] = Sync[Context].delay(head())
}
case class Number1Resource[Context[_], T, U](tail: U => Number1[Context, T], head: Resource[Context, U])(implicit
  v: MonadCancel[Context, Throwable]
) extends Number1[Context, T] {
  def method1(number2: Number2): Context[T] = head.use(s => number2.method2(tail(s)))
}

trait Number2 {
  def method2[Context[_], T](number1: Number1[Context, T]): Context[T]
}
case class Number2S(tail: () => Number2) extends Number2 {
  def method2[Context[_], T](number1: Number1[Context, T]): Context[T] = number1.method1(tail())
}

object NContext {
  trait Number1FlatMap[Context[_], U] {
    def flatMap[T](u: U => Number1[Context, T]): Number1[Context, T]
  }
  trait Number1Map[Context[_], U] {
    def map[T](u: U => T): Number1[Context, T]
  }

  val runner: Number2 = {
    lazy val number2s: Number2 = Number2S(() => number2s)
    number2s
  }

  abstract class PureFlatMap[F[_]] {
    def apply[U](a: => U)(implicit i1: Sync[F], i2: FlatMap[F]): Number1FlatMap[F, U]
  }
  abstract class PureMap[F[_]] {
    def apply[U](a: => U)(implicit i1: Sync[F], i2: FlatMap[F]): Number1Map[F, U]
  }

  def pureFlatMap[F[_]]: PureFlatMap[F] = new PureFlatMap[F] {
    override def apply[U](a: => U)(implicit i1: Sync[F], i2: FlatMap[F]): Number1FlatMap[F, U] = flatMap(Sync[F].delay(a))
  }
  def pureMap[F[_]]: PureMap[F] = new PureMap[F] {
    override def apply[U](a: => U)(implicit i1: Sync[F], i2: FlatMap[F]): Number1Map[F, U] = map(Sync[F].delay(a))
  }

  def flatMap[F[_]: FlatMap, U](a: F[U]): Number1FlatMap[F, U] = new Number1FlatMap[F, U] {
    override def flatMap[T](u: U => Number1[F, T]): Number1[F, T] = Number1S(u, a)
  }
  def map[F[_]: Sync: FlatMap, U](a: F[U]): Number1Map[F, U] = new Number1Map[F, U] {
    override def map[T](fun: U => T): Number1[F, T] = Number1S((u: U) => Number1T(() => fun(u)), a)
  }
  def resource[F[_], U](a: Resource[F, U])(implicit v: MonadCancel[F, Throwable]): Number1FlatMap[F, U] = new Number1FlatMap[F, U] {
    override def flatMap[T](u: U => Number1[F, T]): Number1[F, T] = Number1Resource(u, a)
  }
}
