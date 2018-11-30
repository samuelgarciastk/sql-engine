package io.transwarp.framework.salon

import scala.collection.mutable.ArrayBuffer

abstract class PExpr {
  private val children: ArrayBuffer[PExpr] = new ArrayBuffer[PExpr]

  protected def addChild(expr: PExpr): Unit = children += expr
}

abstract class POperator extends PExpr

class PEqualOperator(left: PExpr, right: PExpr) extends POperator {
  addChild(left)
  addChild(right)

  def calc(data: Map[PSchema, Array[String]]): Boolean = {
    def getValue(expr: PExpr) = expr match {
      case e: PColumnExpr =>
        val t = data.filter { case (s, _) => s.names contains e.columnResolvedName }.toList
        assert(t.size == 1)
        val (schema, line) = t.head
        e.calc(line, schema)
      case e: PSymbolExpression => e.calc
      case _ => throw new RuntimeException("Unknown expression.")
    }

    getValue(left) == getValue(right)
  }
}

class PSymbolExpression(symbol: String) extends PExpr {
  def calc: Int = symbol.toInt
}

class PColumnExpr(val columnResolvedName: String) extends PExpr {
  def calc(line: RowResult, schema: PSchema): Int = {
    val index = schema.names.indexOf(columnResolvedName)
    if (index == -1) throw new RuntimeException("Column not exists.")
    line.array(index)
  }
}

class PSchema(val names: Array[String], val types: Array[DataType.DataType]) {
  def merge(schema: PSchema): PSchema = new PSchema(names ++ schema.names, types ++ schema.types)
}
