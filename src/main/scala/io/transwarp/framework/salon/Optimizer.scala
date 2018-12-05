package io.transwarp.framework.salon

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class Optimizer {
  def optimize(lp: LogicalPlans): PhysicalPlans
}

class FakeOptimizer extends Optimizer {
  override def optimize(lp: LogicalPlans): PhysicalPlans = {
    if (!lp.checkMeta(new FakeTableMetaManager)) throw new RuntimeException("Failed to pass meta check.")

    pushdown(lp)

    val pp = lp.physicalPlan
    pp.genSchema
    pp
  }

  private def pushdown(lp: LogicalPlans): LogicalPlans = {
    var filters = new ArrayBuffer[FilterPlan]
    val planStack = new mutable.Stack[LogicalPlans]

    def recursion(plan: LogicalPlans): Unit = plan match {
      case p: FilterPlan =>
        planStack.push(p)
        filters += p
        p.children.foreach(recursion)
        if (filters contains p) planStack.pop
        else {
          planStack.pop
          val fatherPlan = planStack.top
          fatherPlan.children -= p
          fatherPlan.children ++= p.children
        }
      case p: InputPlan => filters.find(p addFilter).foreach(filters -= _)
      case p =>
        planStack.push(p)
        p.children.foreach(recursion)
        planStack.pop
    }

    recursion(lp)
    lp
  }
}
