package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * @ClassName NotExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 13:35
 * version V1.0
 **/
public class NotExpr extends BoolExpr {
    private BoolExpr innerExpr;

    public NotExpr(ParserRuleContext ctx){
        super(ctx);
        this.innerExpr = BoolExpr.boolExprFactory(ctx);
    }

    public NotExpr(ParserRuleContext ctx, BoolExpr innerExpr){
        super(ctx);
        this.innerExpr = innerExpr;
    }

    @Override
    public StringBuffer print(){
        return  innerExpr.print().insert(0, " (NOT ").append(")");
    }
}
