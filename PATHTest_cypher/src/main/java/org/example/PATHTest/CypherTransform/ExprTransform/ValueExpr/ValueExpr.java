package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.Expression;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr.BoolExpr;

/**
 * @ClassName ValueExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/18 18:17
 * version V1.0
 **/
public class ValueExpr extends Expression {

    private ValueExpr equivalentExpr;
    protected boolean hasPrintEquivalentExpr;

    public ValueExpr(ParserRuleContext ctx) {
        super(ctx);
        hasPrintEquivalentExpr = false;
    }
    public static ValueExpr valueExprFactory(ParserRuleContext ctx){
        int randomValue = random.getInteger(0, 21);
        // todo: node related value generate
        return new ConstValueExpr(ctx);
    }

    @Override
    public String expression_equivalent_transform() {
        ValueExpr ranValue = valueExprFactory(ctx);
        BoolExpr ranBoolExpr = BoolExpr.boolExprFactory(ctx) ;
        return null;
    }

    @Override
    public StringBuffer print() {
        return equivalentExpr.print();
    }
}
