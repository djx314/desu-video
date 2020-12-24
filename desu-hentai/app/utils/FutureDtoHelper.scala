package net.scalax.asuna.sample.dto2

import zsg.macros.multiply.{ZsgMultiplyGeneric, ZsgMultiplyRepGeneric}
import zsg.macros.single.{ZsgGeneric, ZsgSetterGeneric}
import zsg.{ApplicationX4, Context2, Context4, Plus4, PropertyTag, ZsgTuple0}

import scala.concurrent.{ExecutionContext, Future}

trait FutureDtoWrapper[RepTag, DataTag, RepOut, DataType] {
  def rep(rep: RepOut)(implicit ec: ExecutionContext): Future[DataType]
}

object FutureDtoWrapper {
  implicit def futureDtoShapeImplicit1[T]: FutureDtoWrapper[PropertyTag[T], PropertyTag[T], T, T] =
    new FutureDtoWrapper[PropertyTag[T], PropertyTag[T], T, T] {
      override def rep(rep: T)(implicit ec: ExecutionContext): Future[T] = Future.successful(rep)
    }

  implicit def futureDtoShapeImplicit2[T]: FutureDtoWrapper[PropertyTag[Future[T]], PropertyTag[T], Future[T], T] =
    new FutureDtoWrapper[PropertyTag[Future[T]], PropertyTag[T], Future[T], T] {
      override def rep(rep: Future[T])(implicit ec: ExecutionContext): Future[T] = rep
    }
}

trait FutureDtoGetter[DataType] {
  def model(implicit ec: ExecutionContext): Future[DataType]
}

class dtoFContext extends Context4[FutureDtoWrapper] {
  /*override def append[X1, X2, Y1, Y2, Z1, Z2](x: FutureDtoWrapper[X1, X2], y: FutureDtoWrapper[Y1, Y2])(
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
  }*/
  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: FutureDtoWrapper[X1, X2, X3, X4], y: FutureDtoWrapper[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): FutureDtoWrapper[Z1, Z2, Z3, Z4] = {
    new FutureDtoWrapper[Z1, Z2, Z3, Z4] {
      override def rep(rep: Z3)(implicit ec: ExecutionContext): Future[Z4] = x.rep(p.takeHead3(rep)).flatMap(x3 => y.rep(p.takeTail3(rep)).map(y3 => p.plus4(x3, y3)))
    }
  }

  override def start: FutureDtoWrapper[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = new FutureDtoWrapper[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def rep(rep: ZsgTuple0)(implicit ec: ExecutionContext): Future[ZsgTuple0] = Future.successful(rep)
  }
}

object dtoFContext {
  val value: dtoFContext = new dtoFContext
}

trait FutureDtoHelper {

  def dtoWithTable[Model] = new TableApply[Model]

  class TableApply[Model] {
    def apply[Table, R, DataTag, Prop, Nam, Rep](table: Table)(
      implicit ll: ZsgMultiplyGeneric.Aux[Table, Model, R],
      zd: ZsgGeneric.Aux[Model, DataTag],
      app: ApplicationX4[FutureDtoWrapper, dtoFContext, R, DataTag, Rep, Prop],
      repGeneric: ZsgMultiplyRepGeneric[Table, Model, Rep],
      cv3: ZsgSetterGeneric[Model, Prop]
    ): FutureDtoGetter[Model] = {
      val i   = app.application(dtoFContext.value)
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
