package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public class ExpressionStatement implements Statement {
    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
}
