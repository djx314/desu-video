package gd.robot.akka.gdactor.gohome.systemactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GDApp
import gd.robot.akka.utils.GDSystemUtils

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future, Promise}

object WaitForGDFocus {
  trait ActionStatus
  case class InputPromise(promise: ActorRef[Boolean]) extends ActionStatus
  case class CheckGDFocus(focus: Boolean)             extends ActionStatus

  def apply(): Behavior[ActionStatus]                              = Behaviors.setup(s => new WaitForGDFocus(s, List.empty))
  def apply(list: List[ActorRef[Boolean]]): Behavior[ActionStatus] = Behaviors.setup(s => new WaitForGDFocus(s, list))
}

import WaitForGDFocus._
class WaitForGDFocus(context: ActorContext[ActionStatus], promiseList: List[ActorRef[Boolean]])
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
        WaitForGDFocus(promise :: promiseList)
      case CheckGDFocus(focus) =>
        context.pipeToSelf(delayCheck(500))(s => CheckGDFocus(s.getOrElse(false)))
        if (focus) {
          for (p <- promiseList) yield p ! focus
          WaitForGDFocus()
        } else
          Behaviors.same
    }
  }
}
