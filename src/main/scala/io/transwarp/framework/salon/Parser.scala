package io.transwarp.framework.salon

import io.transwarp.framework.salon.antlr.{SqlBaseLexer, SqlBaseParser, SqlBaseVisitor}
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._

import scala.collection.JavaConverters._

abstract class Parser {
  def parse(sql: String): LogicalPlans
}

class FakeParser extends Parser {
  override def parse(sql: String): LogicalPlans = {
    val lexer = new SqlBaseLexer(CharStreams.fromString(sql))
    val tokens = new CommonTokenStream(lexer)
    val parser = new SqlBaseParser(tokens)
    val tree = parser.singleStatement
    val visitor = new FakeVisitor
    visitor.visit(tree)
  }

  private class FakeVisitor extends SqlBaseVisitor[LogicalPlans] {
    override def visitSingleStatement(ctx: SqlBaseParser.SingleStatementContext): LogicalPlans = ctx.statement.accept(this)

    override def visitStatement(ctx: SqlBaseParser.StatementContext): LogicalPlans = {
      val selectPlan = ctx.expressionSeq.accept(this)
      val filterPlan = if (ctx.where != null) {
        val whereExpr = ctx.where.accept(this).asInstanceOf[ExprPlan].expr
        new FilterPlan(whereExpr)
      } else null
      val joinPlan = ctx.fromClause.accept(this)

      assert(joinPlan != null)
      if (filterPlan != null) {
        filterPlan.children += joinPlan
        if (selectPlan != null) {
          selectPlan.children += filterPlan
          selectPlan
        } else filterPlan
      } else {
        if (selectPlan != null) {
          selectPlan.children += joinPlan
          selectPlan
        } else joinPlan
      }
    }

    override def visitExpressionSeq(ctx: SqlBaseParser.ExpressionSeqContext): LogicalPlans =
      if (ctx.WILDCARD() != null) null
      else {
        val expr = ctx.primaryExpression.asScala.map(_.accept(this).asInstanceOf[ExprPlan].expr).toArray
        new SelectPlan(expr)
      }

    override def visitBooleanExpression(ctx: SqlBaseParser.BooleanExpressionContext): LogicalPlans = {
      val leftExpr = ctx.left.accept(this).asInstanceOf[ExprPlan].expr
      val rightExpr = ctx.right.accept(this).asInstanceOf[ExprPlan].expr
      val equalOpr = new EqualOperator(leftExpr, rightExpr)
      new ExprPlan(equalOpr)
    }

    override def visitPrimaryExpression(ctx: SqlBaseParser.PrimaryExpressionContext): LogicalPlans = {
      val expr =
        if (ctx.tbl != null && ctx.col != null) new ColumnExpression(ctx.tbl.getText, ctx.col.getText)
        else if (ctx.constant != null) new SymbolExpression(ctx.constant.getText)
        else {
          assert(ctx.IDENTIFIER().size() == 1)
          new ColumnExpression(null, ctx.IDENTIFIER(0).getSymbol.getText)
        }
      new ExprPlan(expr)
    }

    override def visitFromClause(ctx: SqlBaseParser.FromClauseContext): LogicalPlans = {
      val relations = ctx.relation.asScala.map(_.accept(this))
      var leftRelation = relations.head
      if (relations.length > 1) {
        var i = 1
        while (i < relations.length) {
          val newRelation = new JoinPlan(null)
          newRelation.children += leftRelation
          newRelation.children += relations(i)
          leftRelation = newRelation
          i += 1
        }
      }
      leftRelation
    }

    override def visitRelation(ctx: SqlBaseParser.RelationContext): LogicalPlans = {
      val leftExpr = ctx.relationPrimary.accept(this).asInstanceOf[ExprPlan].expr.asInstanceOf[ColumnExpression]
      val inputPlan = new InputPlan(leftExpr.tbl)
      var leftPlan: LogicalPlans = inputPlan
      ctx.joinRelation.asScala.map(_.accept(this)).foreach(plan => {
        plan.children.insert(0, leftPlan)
        leftPlan = plan
      })
      leftPlan
    }

    override def visitRelationPrimary(ctx: SqlBaseParser.RelationPrimaryContext): LogicalPlans = {
      val expr = new ColumnExpression(ctx.IDENTIFIER.getSymbol.getText, null)
      new ExprPlan(expr)
    }

    override def visitJoinRelation(ctx: SqlBaseParser.JoinRelationContext): LogicalPlans = {
      val rightExpr = ctx.right.accept(this).asInstanceOf[ExprPlan].expr.asInstanceOf[ColumnExpression]
      val inputPlan = new InputPlan(rightExpr.tbl)
      val equalExpr = ctx.booleanExpression.accept(this).asInstanceOf[ExprPlan].expr
      val joinPlan = new JoinPlan(equalExpr)
      joinPlan.children += inputPlan
      joinPlan
    }

    override def visitConstant(ctx: SqlBaseParser.ConstantContext): LogicalPlans = null

    override def visitNumber(ctx: SqlBaseParser.NumberContext): LogicalPlans = null

    override def visit(parseTree: ParseTree): LogicalPlans = parseTree.accept(this)

    override def visitChildren(ruleNode: RuleNode): LogicalPlans = null

    override def visitTerminal(terminalNode: TerminalNode): LogicalPlans = null

    override def visitErrorNode(errorNode: ErrorNode): LogicalPlans = null
  }

}
