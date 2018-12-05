package io.transwarp.framework.salon

abstract class PExpr {
  var schema: PSchema = _

  def setSchema(schema: PSchema): Unit = this.schema = schema

  def setSchema(leftSchema: PSchema, rightSchema: PSchema): Unit = throw new RuntimeException("Unsupported operation.")

  def calc(line: RowResult): Int = throw new RuntimeException("Unsupported operation.")

  def calc(leftLine: RowResult, rightLine: RowResult): Boolean = throw new RuntimeException("Unsupported operation.")
}

abstract class POperator extends PExpr

class PEqualOperator(var left: PExpr, var right: PExpr) extends POperator {
  override def setSchema(leftSchema: PSchema, rightSchema: PSchema): Unit = {
    def judge(schema: PSchema, expr: PExpr): Boolean = expr match {
      case e: PColumnExpr => schema contains e.columnResolvedName
      case _ => false
    }

    def swap(): Unit = {
      val tmp = left
      left = right
      right = tmp
    }

    if (leftSchema == null && rightSchema == null) throw new RuntimeException("Unsupported calculation.")
    if (leftSchema != null && rightSchema != null) {
      val ll = judge(leftSchema, left)
      val lr = judge(leftSchema, right)
      val rl = judge(rightSchema, left)
      val rr = judge(rightSchema, right)
      if ((ll ^ lr) && (rl ^ rr) && (ll ^ rl) && (lr ^ rr)) {
        if (lr) swap()
      } else throw new RuntimeException("Calculation error.")
    } else if (leftSchema == null) {
      val rl = judge(rightSchema, left)
      val rr = judge(rightSchema, right)
      if (rl ^ rr) {
        if (rl) swap()
      } else throw new RuntimeException("Calculation error.")
    } else {
      val ll = judge(leftSchema, left)
      val lr = judge(leftSchema, right)
      if (ll ^ lr) {
        if (lr) swap()
      } else throw new RuntimeException("Calculation error.")
    }
    left.schema = leftSchema
    right.schema = rightSchema
  }

  override def calc(line: RowResult): Int = left calc line

  override def calc(leftLine: RowResult, rightLine: RowResult): Boolean = (left calc leftLine) == (right calc rightLine)
}

class PSymbolExpression(symbol: String) extends PExpr {
  override def calc(line: RowResult): Int = symbol.toInt
}

class PColumnExpr(val columnResolvedName: String) extends PExpr {
  override def calc(line: RowResult): Int = {
    val index = schema.names.indexOf(columnResolvedName)
    line.array(index).toInt
  }
}

class PSchema(val names: Array[String], val types: Array[DataType.DataType]) {
  def merge(schema: PSchema): PSchema = new PSchema(names ++ schema.names, types ++ schema.types)

  def contains(col: String): Boolean = names contains col
}
