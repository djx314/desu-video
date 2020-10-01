package assist.controllers

import archer.controllers.CommonController
import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents

@Singleton
class CommonAssetsController @Inject() (commonAssets: controllers.Assets, controllerComponents: ControllerComponents) extends CommonController(controllerComponents) {

  implicit def ec = defaultExecutionContext

  def staticAt(root: String, path: String) = commonAssets.at(root, path)

}
