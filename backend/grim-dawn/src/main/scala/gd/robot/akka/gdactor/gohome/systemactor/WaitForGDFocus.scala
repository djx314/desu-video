package gd.robot.akka.gdactor.gohome.systemactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future, Promise}

object WaitForGDFocus {
  trait ActionStatus
  case class InputPromise(promise: Promise[Boolean]) extends ActionStatus
  case class CheckGDFocus(focus: Boolean)            extends ActionStatus

  def apply(): Behavior[ActionStatus] = Behaviors.setup(s => new WaitForGDFocus(s))
}

import WaitForGDFocus._
class WaitForGDFocus(context: ActorContext[ActionStatus]) extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private var promiseList: List[Promise[Boolean]] = List.empty

  private def delayCheck[T](million: Long): Future[CheckGDFocus] = Patterns.after(
    Duration(million, MILLISECONDS),
    system.classicSystem.scheduler,
    implicitly[ExecutionContext],
    () => for (focus <- gdSystemUtils.isNowOnFocus) yield CheckGDFocus(focus)
  )

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case InputPromise(keyCode) => promiseList.::=(keyCode)
      case CheckGDFocus(focus) =>
        if (focus) {
          for (p <- promiseList) yield p.trySuccess(true)
          promiseList = List.empty
        }
        context.pipeToSelf(delayCheck(500))(_.getOrElse(CheckGDFocus(false)))
    }
    Behaviors.same
  }
}
