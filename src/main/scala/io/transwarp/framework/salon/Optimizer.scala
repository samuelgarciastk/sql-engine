package io.transwarp.framework.salon

import scala.collection.mutable

abstract class Optimizer {
  def optimize(lp: LogicalPlans): PhysicalPlans
}

class FakeOptimizer extends Optimizer {
  override def optimize(lp: LogicalPlans): PhysicalPlans = {
    if (!lp.checkMeta(new FakeTableMetaManager)) throw new RuntimeException("Failed to pass meta check.")

    // optimize
    var filters = new mutable.HashMap[String, Expr]
    val planStack = new mutable.Stack[LogicalPlans]

    def pushdown(plan: LogicalPlans): Unit = plan match {
      case p: FilterPlan =>
        val tbls = p.expr.children.map {
          case e: ColumnExpression => e.tbl
          case _ => null
        }.filter(_ != null)
        if (tbls.size == 1) filters += tbls.head -> p.expr
        val fatherPlan = planStack.top
        fatherPlan.children -= p
        fatherPlan.children ++= p.children
        p.children.foreach(pushdown)
      case p: InputPlan =>
        if (filters contains p.tableName) {
          val expr = filters(p.tableName)
          p.addFilter(expr)
        }
      case p =>
        planStack.push(p)
        p.children.foreach(pushdown)
        planStack.pop
    }

    pushdown(lp)

    val pp = lp.physicalPlan
    pp.outputSchema()
    pp
  }
}
