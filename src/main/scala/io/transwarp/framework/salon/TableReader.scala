package io.transwarp.framework.salon

import scala.io.Source

/**
  * Read csv table data from tbl_{table_name}.data
  */
abstract class TableReader(tableName: String, filter: PExpr) extends Iterator[RowResult] {}

class FakeTableReader(tableName: String, filter: PExpr, schema: PSchema) extends TableReader(tableName, filter) {
  private var lineIter: Iterator[String] = _
  private var lineFilter: RowResult => Boolean = _
  private var nextLine: RowResult = _

  init()

  override def hasNext: Boolean = {
    var break = false
    while (!break && lineIter.hasNext) {
      val row = new RowResult(lineIter.next.split("\\s+").map(_.toInt))
      if (lineFilter(row)) {
        nextLine = row
        break = true
      }
    }
    break
  }

  override def next: RowResult = nextLine

  private def init(): Unit = {
    val source = Source.fromFile(String.format(FakePath.TABLEPATTERN, tableName))
    lineIter = source.getLines
    source.close
    lineFilter = if (filter != null) row => {
      val equalExpr = filter.asInstanceOf[PEqualOperator]
      (row.array.length == schema.names.length) && equalExpr.calcExpr(row, null)
    } else _ => true
  }
}
