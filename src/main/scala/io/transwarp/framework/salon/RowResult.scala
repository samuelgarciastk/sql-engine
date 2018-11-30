package io.transwarp.framework.salon

class RowResult(val array: Array[Int]) {
  val size: Int = array.length << DataSize.INT
}
