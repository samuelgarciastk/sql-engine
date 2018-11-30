package io.transwarp.framework.salon

object SQLRunner {
  def main(args: Array[String]): Unit = {
    val sql = args(0)
    val parser = new FakeParser
    val lp = parser.parse(sql)

    val optimizer = new FakeOptimizer
    val pp = optimizer.optimize(lp)

    val generator = new FakeExecutionEmitter
    val runner = generator.generateExecution(pp)

    val result = runner.run()

    result.foreach { row =>
      println(row.mkString(","))
    }
  }
}
