package org.example.PATHTest.CypherTransform;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.example.PATHTest.*;
import org.example.PATHTest.CypherTransform.ExprTransform.Expression;
import org.example.PATHTest.CypherTransform.ExprTransform.ExpressionFactory;
import org.example.PATHTest.cypher.CypherGlobalState;
import org.example.PATHTest.cypher.schema.CypherSchema;
import org.example.PATHTest.neo4j.Neo4jProvider;
import org.example.PATHTest.parsercypher.gen.CypherLexer;
import org.example.PATHTest.parsercypher.CypherParser;

import java.util.ArrayList;

/**
 * @ClassName CypherVisitorImpExpression
 * @Description TODO
 * author jinxingui
 * date 2025/3/17 13:29
 * version V1.0
 **/
public class CypherVisitorImpExpression <G extends CypherGlobalState<?, S>, S extends CypherSchema<G, ?>> extends CypherVisitorImp {
    int depth;
    public ArrayList<String> avaliableNodeList;
    public ArrayList<String> avaliableRelationList;

    @Override public String visitExpression(CypherParser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    //todo 其他不需要重载的函数
//    @Override public String visitQuery(CypherParser.QueryContext ctx) {return visitChildren(ctx);}
//    @Override public String visitRegularQuery(CypherParser.RegularQueryContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSkipSt(CypherParser.SkipStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLimitSt(CypherParser.LimitStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitScript(CypherParser.ScriptContext ctx) {return visitChildren(ctx) ;}
//    @Override public String visitProjectionBody(CypherParser.ProjectionBodyContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProjectionItems(CypherParser.ProjectionItemsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProjectionItem(CypherParser.ProjectionItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitOrderItem(CypherParser.OrderItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitOrderSt(CypherParser.OrderStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRemoveSt(CypherParser.RemoveStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRemoveItem(CypherParser.RemoveItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParenExpressionChain(CypherParser.ParenExpressionChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitYieldItems(CypherParser.YieldItemsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitYieldItem(CypherParser.YieldItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMergeSt(CypherParser.MergeStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMergeAction(CypherParser.MergeActionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSetSt(CypherParser.SetStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSetItem(CypherParser.SetItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNodeLabels(CypherParser.NodeLabelsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCreateSt(CypherParser.CreateStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPattern(CypherParser.PatternContext ctx) { return visitChildren(ctx); }
    // todo:对表达式进行等价变异
    @Override public String visitXorExpression(CypherParser.XorExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }

    @Override public String visitAndExpression(CypherParser.AndExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitNotExpression(CypherParser.NotExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitComparisonExpression(CypherParser.ComparisonExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitComparisonSigns(CypherParser.ComparisonSignsContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitAddSubExpression(CypherParser.AddSubExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitMultDivExpression(CypherParser.MultDivExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitPowerExpression(CypherParser.PowerExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitUnaryAddSubExpression(CypherParser.UnaryAddSubExpressionContext ctx) {
        if (ctx.getChildCount() == 1){
            visitChildren(ctx);
        }else if(r.getInteger(0, 6) == 0){
            Expression e = ExpressionFactory.createExpression(ctx);
            result_qurey += e.expression_equivalent_transform();
        }
        return null;
    }
    @Override public String visitAtomicExpression(CypherParser.AtomicExpressionContext ctx) { return null; }
    @Override public String visitListExpression(CypherParser.ListExpressionContext ctx) { return null; }
    @Override public String visitStringExpression(CypherParser.StringExpressionContext ctx) { return null; }
    @Override public String visitStringExpPrefix(CypherParser.StringExpPrefixContext ctx) { return null; }
    @Override public String visitNullExpression(CypherParser.NullExpressionContext ctx) { return null; }
    @Override public String visitPropertyOrLabelExpression(CypherParser.PropertyOrLabelExpressionContext ctx) { return null; }
//    @Override public String visitPropertyExpression(CypherParser.PropertyExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternElem(CypherParser.PatternElemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternElemChain(CypherParser.PatternElemChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProperties(CypherParser.PropertiesContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNodePattern(CypherParser.NodePatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitAtom(CypherParser.AtomContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLhs(CypherParser.LhsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipPattern(CypherParser.RelationshipPatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationDetail(CypherParser.RelationDetailContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipTypes(CypherParser.RelationshipTypesContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFunctionname(CypherParser.FunctionnameContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParenthesizedExpression(CypherParser.ParenthesizedExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFilterWith(CypherParser.FilterWithContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternComprehension(CypherParser.PatternComprehensionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipsChainPattern(CypherParser.RelationshipsChainPatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitListComprehension(CypherParser.ListComprehensionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFilterExpression(CypherParser.FilterExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCountAll(CypherParser.CountAllContext ctx) { return visitChildren(ctx); }
//    @Override public String visitExpressionChain(CypherParser.ExpressionChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCaseExpression(CypherParser.CaseExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParameter(CypherParser.ParameterContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLiteral(CypherParser.LiteralContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRangeLit(CypherParser.RangeLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNumLit(CypherParser.NumLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCharLit(CypherParser.CharLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMapLit(CypherParser.MapLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMapPair(CypherParser.MapPairContext ctx) { return visitChildren(ctx); }
//    @Override public String visitName(CypherParser.NameContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSymbol(CypherParser.SymbolContext ctx) { return visitChildren(ctx); }
//    @Override public String visitReservedWord(CypherParser.ReservedWordContext ctx) { return visitChildren(ctx); }

    public static String transform(CypherVisitorImpExpression cv, String input) {
        cv.Clear();
        CypherLexer lexer = new CypherLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CypherParser parser = new CypherParser(tokens);
        ParseTree tree = parser.script();
        Neo4jProvider provider = new Neo4jProvider();
        try{
            GlobalState state = provider.getGlobalStateClass().getDeclaredConstructor().newInstance();
            cv.setGlobalState((CypherGlobalState) state);
            state.setDatabaseName("neo4j");
            state.setMainOptions(new MainOptions());
        }catch (Exception e){
            e.printStackTrace();
        }

        parser.setBuildParseTree(true);
        cv.visit(tree);
        return cv.Getresult_qurey();
    }

    public static void main(String[] args) {
        String input = "MATCH (a:User {name: \"Alice\"}) -[]->(b:n{name:1}), (b) WHERE a.age>0 " +
                "RETURN [b IN [(a)-[:FOLLOWS]->(b) | b] WHERE b:User | b.name] AS following";
        System.out.println(transform(new CypherVisitorImpExpression(), input));
    }
}
