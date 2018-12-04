package io.transwarp.framework.salon

import scala.collection.mutable.ArrayBuffer

abstract class PlanExecution {
  def run(): Array[Array[String]]
}

abstract class ExecutionEmitter {
  def generateExecution(pp: PhysicalPlans): PlanExecution
}

class FakeExecutionEmitter extends ExecutionEmitter {
  override def generateExecution(pp: PhysicalPlans): PlanExecution = new PlanExecution {
    override def run(): Array[Array[String]] = {
      val buffer = new ArrayBuffer[Array[String]]
      val iter = pp.getRowIterator
      while (iter.hasNext) buffer += iter.next.array
      buffer.toArray
    }
  }
}
