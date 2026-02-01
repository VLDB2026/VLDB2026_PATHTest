package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.ValueExpr;

import java.util.ArrayList;

/**
 * @ClassName ComparisonExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 14:36
 * version V1.0
 **/
public class ComparisonExpr extends BoolExpr {
    private static ArrayList<String> compOperatorList;
    private ValueExpr lhs;
    private ValueExpr rhs;
    private String compOperator;
    public ComparisonExpr(ParserRuleContext ctx){
        super(ctx);
        if (compOperatorList == null){
            compOperatorList = new ArrayList<>();
            compOperatorList.add("=");
            compOperatorList.add("<>");
            compOperatorList.add(">=");
            compOperatorList.add("<=");
            compOperatorList.add(">");
            compOperatorList.add("<");
        }
        compOperator = compOperatorList.get(random.getInteger(0, compOperatorList.size()));
        this.lhs = valueExprFactory(ctx);
        this.rhs = valueExprFactory(ctx);

    }
    public ComparisonExpr(ParserRuleContext ctx, ValueExpr lhs, ValueExpr rhs){
        super(ctx);
        this.lhs = lhs;
        this.rhs = rhs;
        if (compOperatorList == null){
            compOperatorList = new ArrayList<>();
            compOperatorList.add("=");
            compOperatorList.add("<>");
            compOperatorList.add(">=");
            compOperatorList.add("<=");
            compOperatorList.add(">");
            compOperatorList.add("<");
        }
        compOperator = compOperatorList.get(random.getInteger(0, compOperatorList.size()));
    }

    @Override
    public StringBuffer print(){
        return lhs.print().insert(0,"(").append(compOperator).append(rhs.print()).append(")");
    }
}
