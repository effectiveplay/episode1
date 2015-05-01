import play.api._

import java.lang.reflect.Constructor

import models._

object Global extends GlobalSettings {

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[DAO]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(current.dao)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

}
