package io.transwarp.framework.salon

import scala.io.Source

/**
  * Read csv table data from tbl_{table_name}.data
  */
abstract class TableReader(tableName: String, filter: PExpr) {
  def getRowIterator: Iterator[RowResult]
}

class FakeTableReader(tableName: String, filter: PExpr, schema: PSchema) extends TableReader(tableName, filter) {
  override def getRowIterator: Iterator[RowResult] = {
    var iter = Source.fromFile(String.format(FakePath.TABLEPATTERN, tableName))
      .getLines.map(f => new RowResult(f.trim.split("\\s+")))
    if (filter != null)
      iter = iter.filter(f => (f.array.length == schema.names.length) && filter.calc(f, null))
    iter
  }
}
