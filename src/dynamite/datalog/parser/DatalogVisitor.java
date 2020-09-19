// Generated from scripts/Datalog.g4 by ANTLR 4.7.1
package dynamite.datalog.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DatalogParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DatalogVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DatalogParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(DatalogParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#decls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecls(DatalogParser.DeclsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(DatalogParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#inputDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputDecl(DatalogParser.InputDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#outputDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputDecl(DatalogParser.OutputDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#paramMap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamMap(DatalogParser.ParamMapContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#entry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntry(DatalogParser.EntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#relationDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationDecl(DatalogParser.RelationDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#attrs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttrs(DatalogParser.AttrsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#attr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr(DatalogParser.AttrContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#typeDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDecl(DatalogParser.TypeDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRules(DatalogParser.RulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#datalogRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatalogRule(DatalogParser.DatalogRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#heads}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeads(DatalogParser.HeadsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#relPred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelPred(DatalogParser.RelPredContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#expList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpList(DatalogParser.ExpListContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(DatalogParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#cat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCat(DatalogParser.CatContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#bodies}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBodies(DatalogParser.BodiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#datalogPred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatalogPred(DatalogParser.DatalogPredContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#biPred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBiPred(DatalogParser.BiPredContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp(DatalogParser.OpContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#facts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFacts(DatalogParser.FactsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DatalogParser#fact}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFact(DatalogParser.FactContext ctx);
}