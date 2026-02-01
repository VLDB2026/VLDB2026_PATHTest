package org.example.PATHTest.CypherTransform.ExprTransform;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.PATHTest.Randomly;

/**
 * @ClassName Expression
 * @Description TODO
 * author jinxingui
 * date 2025/3/18 17:36
 * version V1.0
 **/
public abstract class Expression {
    protected ParserRuleContext ctx;
    protected String transform_expr;
    protected static Randomly random;
    protected RandomExpressionGenerator ranExprGen;
    enum ExpressionType {
        BOOLEAN,
        NUMBER,
        OTHER
    }
    public Expression(ParserRuleContext ctx) {
        this.ctx = ctx;
        transform_expr = "";
        ranExprGen = new RandomExpressionGenerator();
        if (random == null) {
            random = new Randomly();
        }
    }

    public abstract String expression_equivalent_transform();


    public abstract StringBuffer print();
}
