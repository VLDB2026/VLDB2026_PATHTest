package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.ValueExpr;

/**
 * @ClassName CaseWhenExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 13:48
 * version V1.0
 **/
public class CaseWhenExpr extends ValueExpr {
    private BoolExpr boolExpr;
    private ValueExpr trueExpr;
    private ValueExpr falseExpr;

    public CaseWhenExpr(ParserRuleContext ctx){
        super(ctx);
    }

    public CaseWhenExpr(ParserRuleContext ctx, BoolExpr boolExpr, ValueExpr trueExpr, ValueExpr falseExpr){
        super(ctx);
        this.boolExpr = boolExpr;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }


    public StringBuffer print(){
        return  boolExpr.print().insert(0, "CASE WHEN (").append(") THEN (").append(trueExpr.print()).append(") ELSE (").append(falseExpr.print()).append(") END");
    }


}
