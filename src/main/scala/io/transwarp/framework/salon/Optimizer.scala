package io.transwarp.framework.salon

import scala.collection.mutable.ArrayBuffer

abstract class Optimizer {
  def optimize(lp: LogicalPlans): PhysicalPlans
}

class FakeOptimizer extends Optimizer {
  override def optimize(lp: LogicalPlans): PhysicalPlans = {
    if (!lp.checkMeta(new FakeTableMetaManager)) throw new RuntimeException("Failed to pass meta check.")

    val optimizedLP = pushdown(lp)

    val pp = optimizedLP.physicalPlan
    pp.genSchema
    pp
  }

  private def pushdown(lp: LogicalPlans): LogicalPlans = {
    var filters = new ArrayBuffer[FilterPlan]

    def recursion(plan: LogicalPlans): LogicalPlans = {
      plan match {
        case p: FilterPlan =>
          filters += p
          p.children = p.children.map(recursion)
          if (filters contains p) p else p.children.head
        case p: InputPlan =>
          filters.find(p addFilter).foreach(filters -= _)
          p
        case p =>
          p.children = p.children.map(recursion)
          p
      }
    }

    recursion(lp)
  }
}
