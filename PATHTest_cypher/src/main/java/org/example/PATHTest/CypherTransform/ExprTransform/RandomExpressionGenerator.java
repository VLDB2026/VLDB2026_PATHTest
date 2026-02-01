package org.example.PATHTest.CypherTransform.ExprTransform;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.Randomly;

/**
 * @ClassName RandomExpressionGenerator
 * @Description generate random expression adhere to specified expression type
 * author jinxingui
 * date 2025/3/17 15:45
 * version V1.0
 **/
public class RandomExpressionGenerator {
    Randomly r;
    enum ExpressionType {
        BOOLEAN,
        NUMBER,
        OTHER
    }

    public RandomExpressionGenerator() {
        //todo
        r = new Randomly();

    }

    public String RandomPredicateGenerator(ParserRuleContext ctx) {return null;}

    public String RandomBoolExprGenerator(ParserRuleContext ctx) {



        return null;
    }




    public String expression_factory(ParserRuleContext ctx, ExpressionType exprType) {
        r.getInteger(0, ExpressionType.values().length);
        switch (ExpressionType.values()[r.getInteger(0, ExpressionType.values().length)]) {
            //todo 生成不同类型的表达式
        }
        return "not";
    }
}
