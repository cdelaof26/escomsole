package modeling.parser.expressions;

import modeling.Token;

public record AssignmentExpression(Token name, Token operator, Expression value) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitAssignmentExpression(this);
    }
}
