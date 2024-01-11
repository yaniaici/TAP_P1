package scala

import scala.collection.mutable

class Invoker (totalMemory:Int, controller:Controller, invokerID:String){
  val totalMemoryMB:Int = totalMemory
  val controler:Controller = controller
  val invokerId:String = invokerID
  val actions = new mutable.HashMap[String,Function[Object,Object]]()
  def invokeAction(actionName:String, params:Object):Object = {
    if(!hasAction(actionName)){
      throw new Exception("Action not found")
    }
    val action = actions.get(actionName)
    try {
      action
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error al ejecutar la acción", e)
    }
  }

  def hasAction(action:String):Boolean = {
    actions.contains(action)
  }

}
