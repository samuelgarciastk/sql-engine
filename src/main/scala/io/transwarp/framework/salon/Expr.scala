package io.transwarp.framework.salon

abstract class Expr {
  def checkMeta(tmm: TableMetaManager): Boolean

  def genPExpr: PExpr
}

abstract class Operator extends Expr

class EqualOperator(left: Expr, right: Expr) extends Operator {
  override def checkMeta(tmm: TableMetaManager): Boolean = left.checkMeta(tmm) && right.checkMeta(tmm)

  override def genPExpr: PExpr = new PEqualOperator(left.genPExpr, right.genPExpr)

  def getTableName: String = {
    val tbl = Array(left, right).map {
      case e: ColumnExpression => e.tbl
      case _: SymbolExpression => null
    }.filter(_ != null)
    if (tbl.length == 1) tbl.head else null
  }
}

class SymbolExpression(symbol: String) extends Expr {
  override def checkMeta(tmm: TableMetaManager): Boolean = true

  override def genPExpr: PExpr = new PSymbolExpression(symbol)
}

class ColumnExpression(var tbl: String, col: String) extends Expr {
  override def checkMeta(tmm: TableMetaManager): Boolean = {
    val res = tmm.check(tbl, col)
    if (res && tbl == null) tbl = tmm.getTableName(col)
    res
  }

  override def genPExpr: PExpr = new PColumnExpr(s"$tbl.$col")
}
