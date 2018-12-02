package io.transwarp.framework.salon

abstract class PExpr {
  var schema: PSchema = _
}

abstract class POperator extends PExpr

class PEqualOperator(var left: PExpr, var right: PExpr) extends POperator {
  def calcExpr(leftLine: RowResult, rightLine: RowResult): Boolean = {
    def calc(line: RowResult, expr: PExpr): Int = expr match {
      case e: PSymbolExpression => e.calc
      case e: PColumnExpr => e calc line
      case _ => throw new RuntimeException("Unsupported expression.")
    }

    calc(leftLine, left) == calc(rightLine, right)
  }

  def setSchema(leftSchema: PSchema, rightSchema: PSchema): Unit = {
    def judge(schema: PSchema, expr: PExpr): Boolean = expr match {
      case e: PColumnExpr => schema contains e.columnResolvedName
      case _ => false
    }

    val leftSchemaLeftExpr = judge(leftSchema, left)
    val leftSchemaRightExpr = judge(leftSchema, right)
    val rightSchemaLeftExpr = judge(rightSchema, left)
    val rightSchemaRightExpr = judge(rightSchema, right)

    if (leftSchemaLeftExpr && rightSchemaRightExpr) {
      left.schema = leftSchema
      right.schema = rightSchema
    }
    else if (leftSchemaRightExpr && rightSchemaLeftExpr) {
      val tmp = left
      left = right
      right = tmp
      left.schema = leftSchema
      right.schema = rightSchema
    } else throw new RuntimeException("EqualOperator failed.")
  }
}

class PSymbolExpression(symbol: String) extends PExpr {
  def calc: Int = symbol.toInt
}

class PColumnExpr(val columnResolvedName: String) extends PExpr {
  def calc(line: RowResult): Int = {
    val index = schema.names.indexOf(columnResolvedName)
    line.array(index)
  }
}

class PSchema(val names: Array[String], val types: Array[DataType.DataType]) {
  def merge(schema: PSchema): PSchema = new PSchema(names ++ schema.names, types ++ schema.types)

  def contains(col: String): Boolean = names contains col
}
