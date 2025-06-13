package modeling.parser.expressions;

import modeling.Token;

public record LogicalExpression(Expression leftExpression, Token operator, Expression rightExpression) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitLogicalExpression(this);
    }
}

