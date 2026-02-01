package org.example.PATHTest.CypherTransform.ExprTransform.ValueExpr;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

/**
 * @ClassName BinExpr
 * @Description TODO
 * author jinxingui
 * date 2025/3/19 16:06
 * version V1.0
 **/
public class BinExpr extends ValueExpr {
    private String operator;
    private ValueExpr lhs;
    private ValueExpr rhs;

    private static ArrayList<String> operators;
    public BinExpr(ParserRuleContext ctx){
        super(ctx);

        if(operators == null){}
    }



}
