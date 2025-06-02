package modeling.parser.expressions;

import modeling.Token;

public class AssignmentExpression implements Expression {
    private final Token name;
    private final Token operator;
    private final Expression value;

    public AssignmentExpression(Token name, Token operator, Expression value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }
}
