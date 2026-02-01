package org.example.PATHTest.CypherTransform.ExprTransform;

/**
 * @ClassName BinOperator
 * @Description TODO
 * author jinxingui
 * date 2025/3/20 20:40
 * version V1.0
 **/
public class BinOperator {
    private String name;
    private CypherType operator;
    private CypherType lhs;
    private CypherType rhs;

    public BinOperator(String name, CypherType operator, CypherType lhs, CypherType rhs) {
        this.name = name;
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getName() { return name; }
    public CypherType getOperator() { return operator; }
    public CypherType getLhs() { return lhs; }
    public CypherType getRhs() { return rhs; }
}
