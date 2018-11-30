package io.transwarp.framework.salon

import scala.io.Source

/**
  * Read csv table data from tbl_{table_name}.data
  */
abstract class TableReader(tableName: String, filter: PExpr) extends Iterator[RowResult] {}

class FakeTableReader(tableName: String, filter: PExpr, schema: PSchema) extends TableReader(tableName, filter) {
  private var lineIter: Iterator[String] = _
  private var lineFilter: Array[String] => Boolean = _
  private var nextLine: RowResult = _

  init()

  override def hasNext: Boolean = {
    var break = false
    while (!break && lineIter.hasNext) {
      val elems = lineIter.next.split("\\s+")
      if (lineFilter(elems)) {
        nextLine = new RowResult(elems.map(_.toInt))
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
    lineFilter = if (filter != null) elems => {
      val equalExpr = filter.asInstanceOf[PEqualOperator]
      (elems.length == schema.names.length) && (equalExpr calc Map(schema -> elems))
    } else _ => true
  }
}
