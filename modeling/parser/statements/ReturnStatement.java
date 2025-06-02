package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public class ReturnStatement implements Statement {
    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }
}
