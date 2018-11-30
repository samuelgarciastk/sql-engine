package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.collection.mutable.ArrayBuffer

abstract class PhysicalPlans extends Iterator[ArrayBuffer[RowResult]]{
  val children: ArrayBuffer[PhysicalPlans] = new ArrayBuffer[PhysicalPlans]()
  var schema: PSchema = _

  def addChild(expr: PhysicalPlans): Unit = children += expr

  def outputSchema(): PSchema

  def genMeta(): Unit
}

class PSelectPlan(attr: Array[PExpr]) extends PhysicalPlans {
  override def outputSchema(): PSchema = {
    assert(children.size == 1)
    schema = children.head.outputSchema()
    val cols = attr.map(_.asInstanceOf[PColumnExpr].columnResolvedName)
    val projected = schema.names.filter(cols.contains(_))
    new PSchema(projected, Array.fill(projected.length)(DataType.INT))
  }

  override def run: Array[Array[String]] = {
    val data = children.head.run
    data.map(line => attr.map(_.asInstanceOf[PColumnExpr].calc(line, schema).toString))
  }
}

class PJoinPlan(joinFilter: PExpr) extends PhysicalPlans {
  override def outputSchema(): PSchema = {
    assert(children.size == 2)
    schema = children.head.outputSchema() merge children.last.outputSchema()
    schema
  }

  override def run: Array[Array[String]] = {
    val leftPlan = children.head
    val rightPlan = children.last
    val leftSchema = leftPlan.schema
    val leftData = leftPlan.run
    val rightSchema = rightPlan.schema
    val rightData = rightPlan.run

    if (joinFilter != null) {
      val filter = joinFilter.asInstanceOf[PEqualOperator]
      leftData.flatMap(leftLine =>
        rightData.map(rightLine =>
          if (filter calc Map(leftSchema -> leftLine, rightSchema -> rightLine)) leftLine ++ rightLine
          else null
        ).filter(_ != null)
      )
    } else leftData.flatMap(leftLine => rightData.map(leftLine ++ _))
  }
}

class PScanPlan(tableName: String, pushdownFilter: PExpr, columnSchema: Array[(String, DataType)]) extends PhysicalPlans {
  private val partition = new ArrayBuffer[RowResult]
  private var curSize = 0
  private var tableReader: TableReader = _
  private var nextRow: RowResult = _

  override def outputSchema(): PSchema = schema

  override def genMeta(): Unit = {
    val names = columnSchema.map(f => s"$tableName.${f._1}")
    val types = columnSchema.map(_._2)
    schema = new PSchema(names, types)
    tableReader = new FakeTableReader(tableName, pushdownFilter, schema)
  }

  override def hasNext: Boolean = {
    var break = false
    while (!break && tableReader.hasNext) {
      nextRow = tableReader.next
      if (curSize + nextRow.size > PScanPlan.MAX_SIZE) break = true
      else {
        partition += nextRow
        curSize += nextRow.size
      }
    }
    break || partition.nonEmpty
  }

  override def next(): ArrayBuffer[RowResult] = {
    val res = partition
    partition.clear
    partition += nextRow
    res
  }
}

private object PScanPlan {
  val MAX_SIZE = 1024
}

class PFilterPlan(expr: PExpr) extends PhysicalPlans {
  override def outputSchema(): PSchema = {
    assert(children.size == 1)
    schema = children.head.outputSchema()
    schema
  }

  override def run: Array[Array[String]] = {
    val data = children.head.run
    val filter = expr.asInstanceOf[PEqualOperator]
    data.map(line => if (filter calc Map(schema -> line)) line else null).filter(_ != null)
  }
}
