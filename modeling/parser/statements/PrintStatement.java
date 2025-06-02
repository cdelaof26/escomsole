package modeling.parser.statements;

import modeling.parser.expressions.Expression;

public class PrintStatement implements Statement {
    private final Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }
}
