package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * @ClassName BoolLiteral
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 16:05
 * version V1.0
 **/
public class ConstBool extends BoolExpr {
    enum BoolLiteral {
        TRUE,
        FALSE,
        NULL
    }
    private BoolLiteral boolLiteral;

    public ConstBool(ParserRuleContext ctx){
        super(ctx);
        switch (random.getInteger(0, 3)){
            case 0:
                boolLiteral = BoolLiteral.TRUE;
                break;
            case 1:
                boolLiteral = BoolLiteral.FALSE;
                break;
            case 2:
                boolLiteral = BoolLiteral.NULL;
                break;
            default:
                boolLiteral = BoolLiteral.TRUE;
        }
    }
    @Override
    public StringBuffer print() {
        return new StringBuffer(boolLiteral.toString());
    }
}
