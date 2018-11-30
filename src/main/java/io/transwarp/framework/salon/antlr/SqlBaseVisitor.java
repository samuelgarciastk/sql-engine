// Generated from /home/stk/Projects/Proj1_2/src/main/antlr4/io/transwarp/framework/salon/sql/SqlBase.g4 by ANTLR 4.7
package io.transwarp.framework.salon.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SqlBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SqlBaseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#singleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleStatement(SqlBaseParser.SingleStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(SqlBaseParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#expressionSeq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionSeq(SqlBaseParser.ExpressionSeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(SqlBaseParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#booleanExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanExpression(SqlBaseParser.BooleanExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpression(SqlBaseParser.PrimaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#fromClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromClause(SqlBaseParser.FromClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelation(SqlBaseParser.RelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#relationPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationPrimary(SqlBaseParser.RelationPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#joinRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinRelation(SqlBaseParser.JoinRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(SqlBaseParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link SqlBaseParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SqlBaseParser.NumberContext ctx);
}