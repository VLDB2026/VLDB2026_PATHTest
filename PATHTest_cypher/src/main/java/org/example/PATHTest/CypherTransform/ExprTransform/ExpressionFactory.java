package org.example.PATHTest.CypherTransform.ExprTransform;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.ValueExpr;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr.BoolExpr;
import org.example.PATHTest.parsercypher.CypherParser;

/**
 * @ClassName ExpressionFactory
 * @Description TODO
 * author jinxingui
 * date 2025/3/18 18:33
 * version V1.0
 **/
public class ExpressionFactory {
    public static Expression createExpression(ParserRuleContext ctx) {
        if (ctx instanceof CypherParser.XorExpressionContext || ctx instanceof CypherParser.AndExpressionContext || ctx instanceof CypherParser.NotExpressionContext){
            return new BoolExpr(ctx);
        }else {
            return new ValueExpr(ctx);
        }
    }
}
