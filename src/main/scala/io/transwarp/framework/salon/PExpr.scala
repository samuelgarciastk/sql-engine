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

    if (judge(leftSchema, left)) {
      if (rightSchema == null || judge(rightSchema, right)) {
        left.schema = leftSchema
        right.schema = rightSchema
      } else throw new RuntimeException("EqualOperator failed.")
    } else if (judge(leftSchema, right)) {
      if (rightSchema == null || judge(rightSchema, left)) {
        val tmp = left
        left = right
        right = tmp
        left.schema = leftSchema
        right.schema = rightSchema
      } else throw new RuntimeException("EqualOperator failed.")
    } else throw new RuntimeException("EqualOperator failed.")
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
