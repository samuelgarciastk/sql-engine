package io.transwarp.framework.salon

import org.junit.Test

class Tests {
  private val sql1 =
    """SELECT t1.a, t1.b, t1.c, t1.a1, t1.b1, t2.a, t2.c, t2.a2, t2.b2, t3.a, t3.b, t4.a, t4.b, t4.c, t5.a, t5.c
      |FROM t1 JOIN t2 ON t1.a = t2.b
      |        JOIN t3 ON t2.b = t3.c,
      |     t4 JOIN t5 ON t4.a = t5.b
      |WHERE t1.a = 19
    """.stripMargin
  private val sql2 =
    """SELECT *
      |FROM t1 JOIN t2 ON t1.a = t2.b
      |        JOIN t3 ON t2.b = t3.c,
      |     t4 JOIN t5 ON t4.a = t5.b
      |WHERE t1.a = 19
    """.stripMargin
  private val sql3 =
    """SELECT a1, t1.a, t1.b, t1.c, t1.b1, t2.a, t2.b, t2.c, t2.a2
      |FROM t1 JOIN t1 ON a1 = a1
      |WHERE 1 = a1
    """.stripMargin
  private val sql4 =
    """SELECT *
      |FROM t1 JOIN t2 ON t2.a = t1.b
      |WHERE 1 = a1
    """.stripMargin

  @Test
  def run(): Unit = {
    SQLRunner.main(Array(sql1))
  }
}
