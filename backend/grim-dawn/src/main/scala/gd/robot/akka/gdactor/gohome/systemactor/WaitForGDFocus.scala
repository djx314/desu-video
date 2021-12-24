package gd.robot.akka.gdactor.gohome.systemactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GDApp
import gd.robot.akka.utils.GDSystemUtils

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future}

object WaitForGDFocus {
  trait ActionStatus
  case class InputPromise(promise: ActorRef[Boolean]) extends ActionStatus
  case class CheckGDFocus(focus: Boolean)             extends ActionStatus

  def apply(): Behavior[ActionStatus] = Behaviors.setup(s => new WaitForGDFocus(s, List.empty, false))
  def apply(list: List[ActorRef[Boolean]], focus: Boolean): Behavior[ActionStatus] =
    Behaviors.setup(s => new WaitForGDFocus(s, list, focus))
}

import WaitForGDFocus._
class WaitForGDFocus(context: ActorContext[ActionStatus], promiseList: List[ActorRef[Boolean]], focus: Boolean)
    extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val gdSystemUtils             = GDApp.resource.get[GDSystemUtils]

  private def delayCheck[T](million: Long): Future[Boolean] = {
    Patterns.after(
      Duration(million, MILLISECONDS),
      system.classicSystem.scheduler,
      implicitly[ExecutionContext],
      () => for (focus <- gdSystemUtils.isNowOnFocus) yield focus
    )
  }

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case InputPromise(promise) =>
        if (focus) {
          promise ! focus
          Behaviors.same
        } else
          WaitForGDFocus(promise :: promiseList, focus)
      case CheckGDFocus(focus) =>
        context.pipeToSelf(delayCheck(500))(s => CheckGDFocus(s.getOrElse(false)))
        if (focus) {
          for (p <- promiseList) yield p ! focus
          WaitForGDFocus(List.empty, focus)
        } else
          Behaviors.same
    }
  }
}
