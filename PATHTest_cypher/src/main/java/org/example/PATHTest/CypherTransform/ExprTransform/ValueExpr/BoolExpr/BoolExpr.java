package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.BoolExpr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr.ValueExpr;

/**
 * @ClassName BoolExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/18 18:39
 * version V1.0
 **/
public class BoolExpr extends ValueExpr {
    private BoolExpr equivalentExpr;

    public BoolExpr(ParserRuleContext ctx) {super(ctx);}


    public static BoolExpr boolExprFactory(ParserRuleContext ctx) {
        int randVal = random.getInteger(0, 23);
        if (randVal < 10){
            return new ComparisonExpr(ctx);
        }else if (randVal < 20){
            return new BoolBinExpr(ctx);
        }else if (randVal < 21){
            return new ConstBool(ctx);
        }else if (randVal < 22){
            return new NotExpr(ctx);
        }else if (randVal < 23){
            return new NullPredicateExpr(ctx);
        }
        return null;
    }

    @Override
    public String expression_equivalent_transform() {
        BoolExpr randBoolExpr = boolExprFactory(ctx);

        BoolExpr notRandBoolExpr = new NotExpr(ctx, randBoolExpr);
        BoolExpr isNullRandBoolExpr = new NullPredicateExpr(ctx, randBoolExpr, true);
        BoolExpr isNotNullRandBoolExpr = new NullPredicateExpr(ctx, randBoolExpr, false);

        boolean caseWhenTrue = random.getInteger(0, 2)==0? true: false;
        if (caseWhenTrue){
            BoolBinExpr randOrNotRandBoolExpr = new BoolBinExpr(ctx, true, randBoolExpr, notRandBoolExpr);
            BoolBinExpr truthBoolExpr = new BoolBinExpr(ctx, true, randOrNotRandBoolExpr, isNullRandBoolExpr);
            equivalentExpr = new BoolBinExpr(ctx, true, truthBoolExpr, this);
        }else {
            BoolBinExpr randAndNotRandBoolExpr = new BoolBinExpr(ctx, false, randBoolExpr, notRandBoolExpr);
            BoolBinExpr falseBoolExpr = new BoolBinExpr(ctx, false, randBoolExpr, isNotNullRandBoolExpr);
            equivalentExpr = new BoolBinExpr(ctx, false, falseBoolExpr, this);
        }


        return this.print().toString();

    }

    public StringBuffer print(){
        if (!hasPrintEquivalentExpr){
            hasPrintEquivalentExpr = true;
            return
                    equivalentExpr.print();
        }else {
            hasPrintEquivalentExpr = false;
            return new StringBuffer("\"testSuccess\"");
        }
    }
    // test bool equivalent transform
    public static void main(String[] args) {
        ParserRuleContext ctx = new ParserRuleContext();
        BoolExpr boolExpr =  new BoolExpr(ctx);
        for (int i = 0; i < 100; i++){
            System.out.println(boolExpr.expression_equivalent_transform());
        }

    }

}
