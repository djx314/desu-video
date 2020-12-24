package archer.controllers

import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents}

class CommonController(controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) with Circe
