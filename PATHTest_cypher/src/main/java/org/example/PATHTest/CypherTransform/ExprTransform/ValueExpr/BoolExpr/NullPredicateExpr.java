package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.ValueExpr;

/**
 * @ClassName NullExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 16:05
 * version V1.0
 **/
public class NullPredicateExpr extends BoolExpr {
    private ValueExpr innerExpr;
    private String nullPredicate;
    public NullPredicateExpr(ParserRuleContext ctx, ValueExpr innerExpr, boolean isNullOrNot){
        super(ctx);
        this.innerExpr = innerExpr;
        nullPredicate = isNullOrNot? "IS NULL" : "IS NOT NULL";
    }

    public NullPredicateExpr(ParserRuleContext ctx){
        super(ctx);
        this.innerExpr = ValueExpr.valueExprFactory(ctx);
        nullPredicate = random.getInteger(0,2)==0? "IS NULL" : "IS NOT NULL";
    }

    @Override
    public StringBuffer print() {
        return innerExpr.print().append(nullPredicate);
    }


}
