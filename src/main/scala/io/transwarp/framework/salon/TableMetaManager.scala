package io.transwarp.framework.salon

import io.transwarp.framework.salon.DataType.DataType

import scala.io.Source

/**
  * Read table meta information from db.tables file.
  */
abstract class TableMetaManager {
  def resolve(tableName: String): Array[(String, DataType.DataType)]

  def check(tbl: String, col: String): Boolean

  def getTableName(col: String): String
}

class FakeTableMetaManager extends TableMetaManager {
  private var meta: Map[String, Array[(String, DataType.DataType)]] = _
  private var distinctCol: Map[String, String] = _

  init()

  override def resolve(tableName: String): Array[(String, DataType)] = meta.get(tableName).orNull

  override def check(tbl: String, col: String): Boolean =
    if (tbl != null && col != null) meta.contains(tbl) && meta(tbl).contains((col, DataType.INT))
    else if (tbl == null && col != null) distinctCol contains col
    else if (tbl != null && col == null) meta.keySet contains tbl
    else false

  override def getTableName(col: String): String = distinctCol(col)

  private def init(): Unit = {
    val source = Source.fromFile(FakePath.METAPATH)
    val lines = source.getLines

    distinctCol = Map()

    meta = lines.map(line => {
      val names = line.split(":")
      assert(names.length == 2)
      val tableName = names.head.trim
      val columns = names.last.trim.split("\\s+")
        .map(col => {
          distinctCol += col -> (if (distinctCol contains col) null else tableName)
          (col, DataType.INT)
        })
      tableName -> columns
    }).toMap

    distinctCol = distinctCol.filter(_._2 != null)

    source.close
  }
}
