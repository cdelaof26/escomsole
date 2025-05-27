package modeling.parser.expressions;

import modeling.Token;

public class VariableExpression implements Expression {
    private final Token name;

    public VariableExpression(Token name) {
        this.name = name;
    }
}