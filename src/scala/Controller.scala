package scala

import scala.Invoker

import scala.reflect.internal.util.Collections

class Controller {
  val invokers: Seq[Invoker] => List[Invoker] = List[Invoker]

  def invoke(actionName: String, params: Object):List[Object] = {
    val actionNames = convertActionNameToList(actionName)
    val selectedInvoker = findAvaiableInvoker(128)
    throw new Exception
  }
  def findAvaiableInvoker(requiredMemoryMB: Int) = {
    println(invokers)
    for (invoker <- invokers) {


    }
  }
  def convertActionNameToList(actionName: String): List[String] = List(actionName)
}
