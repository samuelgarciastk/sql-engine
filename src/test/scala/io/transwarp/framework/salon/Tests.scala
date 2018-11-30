package io.transwarp.framework.salon

import org.junit.Test

class Tests {
  private val sql1 =
    """SELECT t1.a, t1.b, t1.c, t1.a1, t1.b1, t2.a, t2.b, t2.c, t2.a2, t2.b2
      |FROM t1 JOIN t2 ON t1.a = t2.b
      |WHERE t1.a = 1
    """.stripMargin
  private val sql2 =
    """SELECT t1.a, t1.b, t1.c, t1.a1, t1.b1, t2.a, t2.c, t2.a2, t2.b2, t3.a, t3.b, t4.a, t4.b, t4.c, t5.a, t5.c
      |FROM t1 JOIN t2 ON t1.a = t2.b
      |        JOIN t3 ON t2.b = t3.c,
      |     t4 JOIN t5 ON t4.a = t5.b
      |WHERE t1.a = 19
    """.stripMargin
  private val sql3 =
    """SELECT a1, b2
      |FROM t1 JOIN t2 ON a1 = b2
      |WHERE a1 = 19
    """.stripMargin

  @Test
  def parse(): Unit = {
    val parser = new FakeParser
    val lp = parser.parse(sql2)
    println
  }

  @Test
  def optimize(): Unit = {
    val parser = new FakeParser
    val lp = parser.parse(sql2)

    val optimizer = new FakeOptimizer
    val pp = optimizer.optimize(lp)

    val generator = new FakeExecutionEmitter
    val runner = generator.generateExecution(pp)

    val result = runner.run()

    result.foreach(row => println(row.mkString(",")))
  }
}
