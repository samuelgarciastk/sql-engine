package io.transwarp.framework.salon

import scala.collection.mutable

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
    var filters = new mutable.HashMap[String, Expr]
    val planStack = new mutable.Stack[LogicalPlans]

    def recursion(plan: LogicalPlans): Unit = plan match {
      case p: FilterPlan =>
        val (tbl, expr) = p.getTableFilter
        if (tbl != null && expr != null) {
          filters += tbl -> expr
          val fatherPlan = planStack.top
          fatherPlan.children -= p
          fatherPlan.children ++= p.children
          p.children.foreach(recursion)
        }
      case p: InputPlan => if (filters contains p.tableName) p.addFilter(filters(p.tableName))
      case p =>
        planStack.push(p)
        p.children.foreach(recursion)
        planStack.pop
    }

    recursion(lp)
    lp
  }
}
