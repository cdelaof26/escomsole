package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public record ReturnStatement(Expression value) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitReturnStatement(this);
    }
}
