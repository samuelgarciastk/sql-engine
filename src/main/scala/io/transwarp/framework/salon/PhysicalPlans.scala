package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.collection.mutable.ArrayBuffer

abstract class PhysicalPlans {
  val children: ArrayBuffer[PhysicalPlans] = new ArrayBuffer[PhysicalPlans]
  var schema: PSchema = _

  def genSchema: PSchema

  def outputSchema: PSchema = schema

  def getRowIterator: Iterator[RowResult]
}

class PSelectPlan(attr: Array[PExpr]) extends PhysicalPlans {
  override def genSchema: PSchema = {
    val originSchema = children.head.genSchema
    val cols = attr.map(f => {
      val expr = f.asInstanceOf[PColumnExpr]
      expr.setSchema(originSchema)
      expr.columnResolvedName
    })
    val projected = originSchema.names.filter(cols.contains(_))
    if (cols.length != projected.length) throw new RuntimeException("Cannot retrieve all columns in SELECT.")
    schema = new PSchema(projected, Array.fill(projected.length)(DataType.INT))
    schema
  }

  override def getRowIterator: Iterator[RowResult] = children.head.getRowIterator.map(f => new RowResult(attr.map(_.calc(f).toString)))
}

abstract class PJoinPlan(joinFilter: PExpr) extends PhysicalPlans {
  override def genSchema: PSchema = {
    val leftSchema = children.head.genSchema
    val rightSchema = children.last.genSchema
    if (joinFilter != null) joinFilter.setSchema(leftSchema, rightSchema)
    schema = leftSchema merge rightSchema
    schema
  }
}

class NestedLoopJoinPlan(joinFilter: PExpr) extends PJoinPlan(joinFilter) {
  private var rowArray: Array[RowResult] = _

  override def getRowIterator: Iterator[RowResult] = {
    build()
    children.head.getRowIterator.flatMap(leftLine =>
      rowArray.filter(f => if (joinFilter != null) joinFilter.calc(leftLine, f) else true)
        .map(rightLine => leftLine merge rightLine)
    )
  }

  private def build(): Unit = rowArray = children.last.getRowIterator.toArray
}

class HashJoinPlan(joinFilter: PExpr) extends PJoinPlan(joinFilter) {
  private var hashMap: Map[String, List[RowResult]] = _

  override def getRowIterator: Iterator[RowResult] = {
    build()
    val leftExpr = joinFilter.asInstanceOf[PEqualOperator].left
    children.head.getRowIterator
      .map(f => (f, leftExpr.calc(f).toString))
      .filter(hashMap contains _._2)
      .flatMap { case (leftLine, key) => hashMap(key).map(leftLine merge) }
  }

  private def build(): Unit = {
    val rightExpr = joinFilter.asInstanceOf[PEqualOperator].right
    hashMap = children.last.getRowIterator
      .map(row => (rightExpr.calc(row).toString, row)).toList
      .groupBy(_._1).mapValues(_.map(_._2))
  }
}

class PScanPlan(tableName: String, pushdownFilter: PExpr, columnSchema: Array[(String, DataType)]) extends PhysicalPlans {
  override def genSchema: PSchema = {
    val names = columnSchema.map(f => s"$tableName.${f._1}")
    val types = columnSchema.map(_._2)
    schema = new PSchema(names, types)
    if (pushdownFilter != null) pushdownFilter.setSchema(schema)
    schema
  }

  override def getRowIterator: Iterator[RowResult] = new FakeTableReader(tableName, pushdownFilter, schema).getRowIterator
}

class PFilterPlan(expr: PExpr) extends PhysicalPlans {
  override def genSchema: PSchema = {
    schema = children.head.genSchema
    expr.setSchema(schema, null)
    schema
  }

  override def getRowIterator: Iterator[RowResult] = children.head.getRowIterator.filter(expr.calc(_, null))
}
