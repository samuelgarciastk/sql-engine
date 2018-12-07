package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.collection.mutable.ArrayBuffer

abstract class LogicalPlans {
  var children: ArrayBuffer[LogicalPlans] = new ArrayBuffer[LogicalPlans]

  def checkMeta(tmm: TableMetaManager): Boolean

  def physicalPlan: PhysicalPlans
}

class SelectPlan(attr: Array[Expr]) extends LogicalPlans {
  override def checkMeta(tmm: TableMetaManager): Boolean = {
    val curRes = attr.map(_.checkMeta(tmm)).reduce(_ && _)
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tmm)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PSelectPlan(attr.map(_.genPExpr))
    children.foreach(pp.children += _.physicalPlan)
    pp
  }
}

class JoinPlan(joinFilter: Expr) extends LogicalPlans {
  override def checkMeta(tmm: TableMetaManager): Boolean = {
    val curRes = if (joinFilter != null) joinFilter.checkMeta(tmm) else true
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tmm)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = if (joinFilter == null) new NestedLoopJoinPlan(null) else new HashJoinPlan(joinFilter.genPExpr)
    children.foreach(pp.children += _.physicalPlan)
    pp
  }
}

class InputPlan(val tableName: String) extends LogicalPlans {
  private var pushdownFilter: Expr = _
  private var columns: Array[(String, DataType)] = _

  override def checkMeta(tmm: TableMetaManager): Boolean = {
    val curRes = tmm.check(tableName, null)
    if (curRes) columns = tmm.resolve(tableName)
    curRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PScanPlan(tableName, if (pushdownFilter != null) pushdownFilter.genPExpr else null, columns)
    children.foreach(pp.children += _.physicalPlan)
    pp
  }

  def addFilter(filterPlan: FilterPlan): Boolean = if (pushdownFilter == null) {
    val tbl = filterPlan.expr.asInstanceOf[EqualOperator].getTableName
    if (tbl != null && tbl == tableName) {
      pushdownFilter = filterPlan.expr
      true
    } else false
  } else false
}

class FilterPlan(val expr: Expr) extends LogicalPlans {
  override def checkMeta(tmm: TableMetaManager): Boolean = {
    val curRes = expr.checkMeta(tmm)
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tmm)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PFilterPlan(expr.genPExpr)
    children.foreach(pp.children += _.physicalPlan)
    pp
  }
}

class ExprPlan(val expr: Expr) extends LogicalPlans {
  override def checkMeta(tmm: TableMetaManager): Boolean = throw new RuntimeException("Unsupported.")

  override def physicalPlan: PhysicalPlans = throw new RuntimeException("Unsupported.")
}
