package modeling.parser.statements;

import modeling.parser.expressions.Expression;
import modeling.Token;

public class VariableStatement implements Statement {
    private final Token name;
    private final Expression initializer;

    public VariableStatement(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }
}
