// Generated from /home/stk/Projects/Proj1_2/src/main/antlr4/io/transwarp/framework/salon/sql/SqlBase.g4 by ANTLR 4.7
package io.transwarp.framework.salon.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SqlBaseParser}.
 */
public interface SqlBaseListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(SqlBaseParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(SqlBaseParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#expressionSeq}.
	 * @param ctx the parse tree
	 */
	void enterExpressionSeq(SqlBaseParser.ExpressionSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#expressionSeq}.
	 * @param ctx the parse tree
	 */
	void exitExpressionSeq(SqlBaseParser.ExpressionSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void enterBooleanExpression(SqlBaseParser.BooleanExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 */
	void exitBooleanExpression(SqlBaseParser.BooleanExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(SqlBaseParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(SqlBaseParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void enterFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void exitFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void enterRelationPrimary(SqlBaseParser.RelationPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 */
	void exitRelationPrimary(SqlBaseParser.RelationPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 */
	void enterJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 */
	void exitJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(SqlBaseParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(SqlBaseParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SqlBaseParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SqlBaseParser.NumberContext ctx);
}