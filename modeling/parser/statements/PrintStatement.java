package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public record PrintStatement(Expression expression) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitPrintStatement(this);
    }
}
