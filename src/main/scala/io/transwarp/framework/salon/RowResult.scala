package io.transwarp.framework.salon

class RowResult(val array: Array[String]) {
  def merge(row: RowResult): RowResult = new RowResult(array ++ row.array)
}
