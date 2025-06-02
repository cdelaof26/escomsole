package modeling.parser.expressions;

import modeling.Token;

public class ArithmeticExpression implements Expression {
    private final Expression leftExpression;
    private final Token operator;
    private final Expression rightExpression;

    public ArithmeticExpression(Expression leftExpression, Token operator, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
}
