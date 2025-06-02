package modeling.parser.expressions;

import modeling.Token;

public class UnaryExpression implements Expression {
    private final Token operator;
    private final Expression rightExpression;

    public UnaryExpression(Token operator, Expression rightExpression) {
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
}
