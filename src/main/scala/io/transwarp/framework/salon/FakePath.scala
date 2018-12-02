package io.transwarp.framework.salon

private[salon] object FakePath {
  val ROOTPATH = "/home/stk/Projects/Proj1_2/src/test/resources"
  val META = "db.tables"
  val METAPATH = s"$ROOTPATH/$META"
  val TABLEPATTERN: String = ROOTPATH + "/tbl_%1$s.data"
}
