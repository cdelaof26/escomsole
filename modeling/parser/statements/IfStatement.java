package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public record IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitIfStatement(this);
    }
}
