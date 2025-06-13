package modeling.parser.expressions;

import modeling.Token;

public record RelationalExpression(Expression leftExpression, Token operator, Expression rightExpression) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitRelationalExpression(this);
    }
}
