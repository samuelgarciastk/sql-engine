package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class PhysicalPlans extends Iterator[RowResult] {
  val children: ArrayBuffer[PhysicalPlans] = new ArrayBuffer[PhysicalPlans]
  var schema: PSchema = _

  def genSchema(): Unit

  def outputSchema: PSchema = schema
}

class PSelectPlan(attr: Array[PExpr]) extends PhysicalPlans {
  private var originSchema: PSchema = _

  override def genSchema(): Unit = {
    originSchema = children.head.outputSchema
    val cols = attr.map(_.asInstanceOf[PColumnExpr].columnResolvedName)
    val projected = originSchema.names.filter(cols.contains(_))
    if (cols.length != projected.length) throw new RuntimeException("Cannot retrieve all columns in SELECT.")
    schema = new PSchema(projected, Array.fill(projected.length)(DataType.INT))
  }

  override def hasNext: Boolean = children.head.hasNext

  override def next: RowResult = {
    val row = children.head.next
    new RowResult(attr.map(_.asInstanceOf[PColumnExpr].calc(row, originSchema)))
  }
}

class PJoinPlan(joinFilter: PExpr) extends PhysicalPlans {
  private val rowBuffer = new mutable.Queue[RowResult]
  private var hashMap: mutable.HashMap[String, ArrayBuffer[RowResult]] = _
  private var rowIter: Iterator[RowResult] = _

  override def genSchema(): Unit = schema = children.head.outputSchema merge children.last.outputSchema

  //  override def run: Array[Array[String]] = {
  //    val leftPlan = children.head
  //    val rightPlan = children.last
  //    val leftSchema = leftPlan.schema
  //    val leftData = leftPlan.run
  //    val rightSchema = rightPlan.schema
  //    val rightData = rightPlan.run
  //
  //    if (joinFilter != null) {
  //      val filter = joinFilter.asInstanceOf[PEqualOperator]
  //      leftData.flatMap(leftLine =>
  //        rightData.map(rightLine =>
  //          if (filter calc Map(leftSchema -> leftLine, rightSchema -> rightLine)) leftLine ++ rightLine
  //          else null
  //        ).filter(_ != null)
  //      )
  //    } else leftData.flatMap(leftLine => rightData.map(leftLine ++ _))
  //  }

  override def hasNext: Boolean = if (rowBuffer.isEmpty) {
    if (hashMap == null) build()
  } else true

  private def build(): Unit = {
    val map = new mutable.HashMap[String, ArrayBuffer[RowResult]]
    val leftPlan = children.head
    val leftSchema = leftPlan.schema
    val leftExpr = joinFilter.asInstanceOf[PEqualOperator].left.asInstanceOf[PColumnExpr]
    while (leftPlan.hasNext) {
      val row = leftPlan.next
      val key = leftExpr.calc(row, leftSchema)
      if (map contains row)
    }

    hashMap = map
    rowIter = children.last
  }

  override def next: RowResult = rowBuffer.dequeue
}

class PScanPlan(tableName: String, pushdownFilter: PExpr, columnSchema: Array[(String, DataType)]) extends PhysicalPlans {
  private var tableReader: TableReader = _

  override def genSchema(): Unit = {
    val names = columnSchema.map(f => s"$tableName.${f._1}")
    val types = columnSchema.map(_._2)
    schema = new PSchema(names, types)
    tableReader = new FakeTableReader(tableName, pushdownFilter, schema)
  }

  override def hasNext: Boolean = tableReader.hasNext

  override def next: RowResult = tableReader.next
}

class PFilterPlan(expr: PExpr) extends PhysicalPlans {
  private var nextRow: RowResult = _

  override def genSchema(): Unit = schema = children.head.outputSchema

  override def hasNext: Boolean = {
    val iter = children.head
    val filter = expr.asInstanceOf[PEqualOperator]
    var break = false
    while (!break && iter.hasNext) {
      nextRow = iter.next
      if (filter.calcExpr(nextRow, null)) break = true
    }
    break
  }

  override def next: RowResult = nextRow
}
