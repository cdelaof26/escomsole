package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public record LoopStatement(Expression condition, Statement body) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitLoopStatement(this);
    }
}
