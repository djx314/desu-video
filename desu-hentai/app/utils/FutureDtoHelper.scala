package net.scalax.asuna.sample.dto2

import asuna.{Application2, AsunaTuple0, Context2, Plus2, PropertyTag1, TupleTag}
import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import asuna.macros.single.AsunaSetterGeneric

import scala.concurrent.{ExecutionContext, Future}

trait FutureDtoWrapper[RepOut, DataType] {
  def rep(rep: RepOut)(implicit ec: ExecutionContext): Future[DataType]
}

object FutureDtoWrapper {
  implicit def futureDtoShapeImplicit1[T]: Application2[FutureDtoWrapper, PropertyTag1[T, T], T, T] =
    new Application2[FutureDtoWrapper, PropertyTag1[T, T], T, T] {
      override def application(context: Context2[FutureDtoWrapper]): FutureDtoWrapper[T, T] = {
        new FutureDtoWrapper[T, T] {
          override def rep(rep: T)(implicit ec: ExecutionContext): Future[T] = {
            Future.successful(rep)
          }
        }
      }
    }

  implicit def futureDtoShapeImplicit2[T]: Application2[FutureDtoWrapper, PropertyTag1[Future[T], T], Future[T], T] =
    new Application2[FutureDtoWrapper, PropertyTag1[Future[T], T], Future[T], T] {
      override def application(context: Context2[FutureDtoWrapper]): FutureDtoWrapper[Future[T], T] = {
        new FutureDtoWrapper[Future[T], T] {
          override def rep(rep: Future[T])(implicit ec: ExecutionContext): Future[T] = {
            rep
          }
        }
      }
    }
}

trait FutureDtoGetter[DataType] {
  def model(implicit ec: ExecutionContext): Future[DataType]
}

trait FutureDtoHelper {

  object dtoFContext extends Context2[FutureDtoWrapper] {
    override def append[X1, X2, Y1, Y2, Z1, Z2](x: FutureDtoWrapper[X1, X2], y: FutureDtoWrapper[Y1, Y2])(
      p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
    ): FutureDtoWrapper[Z1, Z2] = {
      new FutureDtoWrapper[Z1, Z2] {
        override def rep(rep: Z1)(implicit ec: ExecutionContext): Future[Z2] = {
          x.rep(p.takeHead1(rep)).flatMap(x2 => y.rep(p.takeTail1(rep)).map(y2 => p.plus2(x2, y2)))
        }
      }
    }
    override def start: FutureDtoWrapper[AsunaTuple0, AsunaTuple0] = {
      new FutureDtoWrapper[AsunaTuple0, AsunaTuple0] {
        override def rep(rep: AsunaTuple0)(implicit ec: ExecutionContext): Future[AsunaTuple0] = {
          Future.successful(AsunaTuple0.value)
        }
      }
    }
  }

  def dtoWithTable[Model] = new TableApply[Model]

  class TableApply[Model] {
    def apply[Table, R <: TupleTag, Prop, Nam, Rep](table: Table)(
      implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
      app: Application2[FutureDtoWrapper, R, Rep, Prop],
      repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
      cv3: AsunaSetterGeneric[Model, Prop]
    ): FutureDtoGetter[Model] = {
      val i   = app.application(dtoFContext)
      val rep = repGeneric.rep(table)
      new FutureDtoGetter[Model] {
        override def model(implicit ec: ExecutionContext): Future[Model] = {
          i.rep(rep).map(r => cv3.setter(r))
        }
      }
    }
  }

  /*object dtoF extends DecoderHelper[Future[(Any, Any)], (Any, Any)] with DecoderWrapperHelper[Future[(Any, Any)], (Any, Any), FutureDtoWrapper] {
    override def effect[Rep, D, Out](rep: Rep)(implicit shape: DecoderShape.Aux[Rep, D, Out, Future[(Any, Any)], (Any, Any)]): FutureDtoWrapper[Out, D] = {
      val wrapCol = shape.wrapRep(rep)
      val colsF   = shape.toLawRep(wrapCol, Future.successful(((), ())))
      new FutureDtoWrapper[Out, D] {
        override def model(implicit ec: ExecutionContext): Future[D] = {
          val data = colsF.map(cols => shape.takeData(wrapCol, cols))
          data.map(_.current)
        }
      }
    }
  }*/

  /*implicit def futureDtoShapeImplicit1[T](implicit ec: ExecutionContext): DecoderShape.Aux[RepColumnContent[T, T], T, T, Future[(Any, Any)], (Any, Any)] =
    new DecoderShape[RepColumnContent[T, T], Future[(Any, Any)], (Any, Any)] {
      override type Target = T
      override type Data   = T
      override def wrapRep(base: RepColumnContent[T, T]): T                          = base.rep
      override def toLawRep(base: T, oldRep: Future[(Any, Any)]): Future[(Any, Any)] = oldRep.map(r => (base, r))
      override def takeData(rep: T, oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(current = oldData._1.asInstanceOf[T], left = oldData._2.asInstanceOf[(Any, Any)])
    }

  implicit def futureDtoShapeImplicit2[T](
      implicit ec: ExecutionContext
  ): DecoderShape.Aux[RepColumnContent[Future[T], T], T, Future[T], Future[(Any, Any)], (Any, Any)] =
    new DecoderShape[RepColumnContent[Future[T], T], Future[(Any, Any)], (Any, Any)] {
      override type Target = Future[T]
      override type Data   = T
      override def wrapRep(base: RepColumnContent[Future[T], T]): Future[T] = base.rep
      override def toLawRep(base: Future[T], oldRep: Future[(Any, Any)]): Future[(Any, Any)] = {
        for {
          oldData  <- oldRep
          baseData <- base
        } yield {
          (baseData, oldData)
        }
      }
      override def takeData(rep: Future[T], oldData: (Any, Any)): SplitData[T, (Any, Any)] =
        SplitData(current = oldData._1.asInstanceOf[T], left = oldData._2.asInstanceOf[(Any, Any)])
    }*/

}
