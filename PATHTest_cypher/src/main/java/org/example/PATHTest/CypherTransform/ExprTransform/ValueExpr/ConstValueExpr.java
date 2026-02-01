package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * @ClassName ConstValueExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 19:46
 * version V1.0
 **/
public class ConstValueExpr extends ValueExpr {
    enum ConstType{
        NODEPROPERTY,
        NUMBERLITERAL,
        STRINGLITERAL,
        BOOLEANLITERAL,
        DATELITERAL,
        TIMELITERAL,
        DATETIMELITERAL,
    }


    private String constValue;
    public ConstValueExpr(ParserRuleContext ctx){
        super(ctx);
        constValue = "\"MagicValue\"";
    }

    @Override public StringBuffer  print() { return new StringBuffer(this.constValue);}
}
