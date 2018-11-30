package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.collection.mutable.ArrayBuffer

abstract class LogicalPlans {
  val children: ArrayBuffer[LogicalPlans] = new ArrayBuffer[LogicalPlans]()

  def addChild(expr: LogicalPlans): Unit = children += expr

  def checkMeta(tableMetaManager: TableMetaManager): Boolean

  def physicalPlan: PhysicalPlans
}

class SelectPlan(attr: Array[Expr]) extends LogicalPlans {
  override def checkMeta(tableMetaManager: TableMetaManager): Boolean = {
    val curRes = attr.map(expr => {
      val id = expr.asInstanceOf[ColumnExpression]
      val res = tableMetaManager.check(id.tbl, id.col)
      if (res && id.tbl == null) id.tbl = tableMetaManager.getTableName(id.col)
      res
    }).reduce(_ && _)
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tableMetaManager)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PSelectPlan(attr.map(_.genPExpr))
    children.foreach(lp => pp.addChild(lp.physicalPlan))
    pp
  }
}

class JoinPlan(joinFilter: Expr) extends LogicalPlans {
  override def checkMeta(tableMetaManager: TableMetaManager): Boolean = {
    val curRes = if (joinFilter != null) {
      val equalExpr = joinFilter.asInstanceOf[EqualOperator]
      val left = equalExpr.left match {
        case id: ColumnExpression =>
          val res = tableMetaManager.check(id.tbl, id.col)
          if (res && id.tbl == null) id.tbl = tableMetaManager.getTableName(id.col)
          res
        case _ => true
      }
      val right = equalExpr.right match {
        case id: ColumnExpression =>
          val res = tableMetaManager.check(id.tbl, id.col)
          if (res && id.tbl == null) id.tbl = tableMetaManager.getTableName(id.col)
          res
        case _ => true
      }
      left && right
    } else true
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tableMetaManager)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PJoinPlan(if (joinFilter != null) joinFilter.genPExpr else null)
    children.foreach(lp => pp.addChild(lp.physicalPlan))
    pp
  }
}

class InputPlan(val tableName: String) extends LogicalPlans {
  private var pushdownFilter: Expr = _
  private var columns: Array[(String, DataType)] = _

  override def checkMeta(tableMetaManager: TableMetaManager): Boolean = {
    val curRes = tableMetaManager.check(tableName, null)
    if (curRes) columns = tableMetaManager.resolve(tableName)
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tableMetaManager)).reduce(_ && _) else true
    curRes && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PScanPlan(tableName, if (pushdownFilter != null) pushdownFilter.genPExpr else null, columns)
    children.foreach(lp => pp.addChild(lp.physicalPlan))
    pp
  }

  def addFilter(expr: Expr): Unit = pushdownFilter = expr
}

class FilterPlan(val expr: Expr) extends LogicalPlans {
  override def checkMeta(tableMetaManager: TableMetaManager): Boolean = {
    assert(expr != null)
    val equalExpr = expr.asInstanceOf[EqualOperator]
    val left = equalExpr.left match {
      case id: ColumnExpression =>
        val res = tableMetaManager.check(id.tbl, id.col)
        if (res && id.tbl == null) id.tbl = tableMetaManager.getTableName(id.col)
        res
      case _ => true
    }
    val right = equalExpr.right match {
      case id: ColumnExpression =>
        val res = tableMetaManager.check(id.tbl, id.col)
        if (res && id.tbl == null) id.tbl = tableMetaManager.getTableName(id.col)
        res
      case _ => true
    }
    val childrenRes = if (children.nonEmpty) children.map(_.checkMeta(tableMetaManager)).reduce(_ && _) else true
    left && right && childrenRes
  }

  override def physicalPlan: PhysicalPlans = {
    val pp = new PFilterPlan(expr.genPExpr)
    children.foreach(lp => pp.addChild(lp.physicalPlan))
    pp
  }
}

class ExprPlan(val expr: Expr) extends LogicalPlans {
  override def checkMeta(tableMetaManager: TableMetaManager): Boolean = throw new RuntimeException("Unsupported.")

  override def physicalPlan: PhysicalPlans = throw new RuntimeException("Unsupported.")
}

abstract class Expr {
  val children: ArrayBuffer[Expr] = new ArrayBuffer[Expr]()

  def genPExpr: PExpr

  protected def addChild(expr: Expr): Unit = children += expr
}

abstract class Operator extends Expr

class EqualOperator(val left: Expr, val right: Expr) extends Operator {
  addChild(left)
  addChild(right)

  override def genPExpr: PExpr = new PEqualOperator(left.genPExpr, right.genPExpr)
}

class SymbolExpression(symbol: String) extends Expr {
  override def genPExpr: PExpr = new PSymbolExpression(symbol)
}

class ColumnExpression(var tbl: String, var col: String) extends Expr {
  override def genPExpr: PExpr = new PColumnExpr(s"$tbl.$col")
}

class TableExpression(val tbl: String) extends Expr {
  override def genPExpr: PExpr = throw new RuntimeException("Not supported.")
}