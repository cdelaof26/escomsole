package modeling.parser.expressions;

import modeling.Token;

public class LogicalExpression implements Expression {
    private final Expression leftExpression;
    private final Token operator;
    private final Expression rightExpression;

    public LogicalExpression(Expression leftExpression, Token operator, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
}

