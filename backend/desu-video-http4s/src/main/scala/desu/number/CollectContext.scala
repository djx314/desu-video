package desu.number

import cats._
import cats.implicits._
import cats.effect._
import cats.effect.kernel.MonadCancel

trait CollectFlatMap[F[_], A] {
  def f[T](fun: A => F[T]): F[T]
}

trait Number[F[_], A] {
  def execute[T <: TypeContext](contexts: Context[T, F, A])(s: T#Parameter, t: T#toDataType): T#Result
}
case class NumberS[F[_], A, X](tail: X => Number[F, A], head: CollectFlatMap[F, X]) extends Number[F, A] {
  override def execute[T <: TypeContext](context: Context[T, F, A])(parameter: T#Parameter, t: T#toDataType): T#Result = {
    val newDataCtx = context.convertS(t, tail)
    context.bindS(newDataCtx, parameter, head)
  }
}
case class NumberT[F[_], A](tail: () => Number[F, A], head: F[A]) extends Number[F, A] {
  override def execute[T <: TypeContext](context: Context[T, F, A])(parameter: T#Parameter, t: T#toDataType): T#Result = {
    val newDataCtx = context.convertT(t, tail)
    context.bindT(newDataCtx, parameter, head)
  }
}

trait Context[T <: TypeContext, F[_], A] {
  type DataCtxS[X]
  type DataCtxT
  def convertS[U](t: T#toDataType, current: U => Number[F, A]): DataCtxS[U]
  def convertT(t: T#toDataType, current: () => Number[F, A]): DataCtxT
  def bindS[U](number: DataCtxS[U], parameter: T#Parameter, head: CollectFlatMap[F, U]): T#Result
  def bindT(number: DataCtxT, parameter: T#Parameter, head: F[A]): T#Result
}

trait TypeContext {
  type toDataType
  type Parameter
  type Result
}

class CollectContext[F[_]] {

  trait NumberFlatMap[U] {
    def flatMap[T](u: U => Number[F, T]): Number[F, T]
  }
  trait NumberMap[U] {
    def map[T](u: U => T): Number[F, T]
  }

  def flatMap[U](a: F[U])(implicit i: FlatMap[F]): NumberFlatMap[U] = new NumberFlatMap[U] {
    override def flatMap[T](u: U => Number[F, T]): Number[F, T] = NumberS(
      u,
      new CollectFlatMap[F, U] {
        override def f[T](fun: U => F[T]): F[T] = a.flatMap(fun)
      }
    )
  }
  def map[U](a: F[U])(implicit i1: Functor[F]): NumberMap[U] = new NumberMap[U] {
    override def map[T](fun: U => T): Number[F, T] = {
      def numbert: NumberT[F, T] = NumberT(() => numbert, Functor[F].map(a)(fun))
      numbert
    }
  }
  def resource_use[U](a: Resource[F, U])(implicit v: MonadCancel[F, Throwable]): NumberFlatMap[U] = new NumberFlatMap[U] {
    override def flatMap[T](u: U => Number[F, T]): Number[F, T] = NumberS(
      u,
      new CollectFlatMap[F, U] {
        override def f[T](fun: U => F[T]): F[T] = a.use(fun)
      }
    )
  }

  def runF[Data](number: Number[F, Data]): F[Data] = number.execute(Runner.runner)((), ())

  private object Runner {
    class TypeContextData[Data] extends TypeContext {
      override type toDataType = Unit
      override type Parameter  = Unit
      override type Result     = F[Data]
    }

    def runner[Data]: Context[TypeContextData[Data], F, Data] = new Context[TypeContextData[Data], F, Data] {
      override type DataCtxS[X] = X => Number[F, Data]
      override type DataCtxT    = () => Number[F, Data]
      override def convertS[U](t: Unit, current: U => Number[F, Data]): U => Number[F, Data] = current
      override def convertT(t: Unit, current: () => Number[F, Data]): () => Number[F, Data]  = current
      override def bindS[U](number: U => Number[F, Data], parameter: Unit, head: CollectFlatMap[F, U]): F[Data] =
        head.f(u => number(u).execute(this)((), ()))
      override def bindT(number: () => Number[F, Data], parameter: Unit, head: F[Data]): F[Data] = head
    }
  }

}
