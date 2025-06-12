package modeling.parser.expressions;

import modeling.Token;

public record VariableExpression(Token name) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitVariableExpression(this);
    }
}