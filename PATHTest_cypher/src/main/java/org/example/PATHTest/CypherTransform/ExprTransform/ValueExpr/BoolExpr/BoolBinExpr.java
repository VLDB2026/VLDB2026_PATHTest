package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * @ClassName BoolBinExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 14:35
 * version V1.0
 **/
public class BoolBinExpr extends BoolExpr {
    enum Operator {
        AND,
        OR,
        XOR
    }
    private BoolExpr lhs;
    private BoolExpr rhs;
    private Operator operator;
    public BoolBinExpr(ParserRuleContext ctx){
        super(ctx);
        switch (random.getInteger(0,3)){
            case 0:
                operator = Operator.AND;
                break;
            case 1:
                operator = Operator.OR;
                break;
            case 2:
                operator = Operator.XOR;
                break;
            default:
                operator = Operator.OR;
        }
        this.lhs = BoolExpr.boolExprFactory(ctx);
        this.rhs = BoolExpr.boolExprFactory(ctx);
    };
    public BoolBinExpr(ParserRuleContext ctx, boolean isOr, BoolExpr lhs, BoolExpr rhs){
        super(ctx);
        operator = isOr ? Operator.OR : Operator.AND;
        this.lhs = lhs;
        this.rhs = rhs;

    }

    @Override
    public StringBuffer print() {
        return lhs.print().insert(0, "(").append(operator.toString()).append(rhs.print()).append(")");
    }


}
