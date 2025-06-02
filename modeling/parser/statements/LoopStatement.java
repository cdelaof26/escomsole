package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public class LoopStatement implements Statement {
    private final Expression condition;
    private final Statement body;

    public LoopStatement(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }
}
