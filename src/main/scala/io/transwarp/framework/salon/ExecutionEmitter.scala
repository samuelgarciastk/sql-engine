package io.transwarp.framework.salon

abstract class PlanExecution {
  def run(): Array[Array[String]]
}

abstract class ExecutionEmitter {
  def generateExecution(pp: PhysicalPlans): PlanExecution
}

class FakeExecutionEmitter extends ExecutionEmitter {
  override def generateExecution(pp: PhysicalPlans): PlanExecution = new PlanExecution {
    override def run(): Array[Array[String]] = pp.run
  }
}
