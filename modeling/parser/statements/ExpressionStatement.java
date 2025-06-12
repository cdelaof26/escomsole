package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public record ExpressionStatement(Expression expression) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}
