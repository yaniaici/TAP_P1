package scala

import scala.collection.mutable

class Invoker (totalMemory:Int, controller:Controller, invokerID:String){
  var usedMemory = 0
  val totalMemoryMB:Int = totalMemory
  val controler:Controller = controller
  val invokerId:String = invokerID
  val actions = new mutable.HashMap[String,Function[Object,Object]]

  def getInvokerId() = invokerID

  def getController() = controller
  def registerAction(actionName:String, action:Function[Object,Object], memoryMB: Int ) = {
    if(getFreeMemory() > memoryMB){
      actions.put(actionName,action)
      usedMemory = usedMemory + memoryMB
      println(usedMemory)
    }else{
      println(s"No hay suficiente memoria para registrar la acción $action")
    }
  }
  def invokeAction(actionName:String, params:Object):Object = {
    if(!hasAction(actionName)){
      throw new Exception("Action not found")
    }
    val action = actions(actionName)
    try {
      action.apply(params)
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error al ejecutar la acción", e)
    }
  }

  def hasAction(action:String):Boolean = {
    actions.contains(action)
  }

  def getFreeMemory() = totalMemoryMB - usedMemory

  def getUsedMemory() = usedMemory


  override def toString = s"Invoker(InvokerId = $invokerId,Total Memory = $totalMemoryMB, Used Memory = $usedMemory, Actions = $actions)"
}
