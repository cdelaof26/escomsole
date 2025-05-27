package modeling.parser.expressions;

import modeling.Token;

public class AssignmentExpression implements Expression {
    private final Token name;
    private final Expression value;

    public AssignmentExpression(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
