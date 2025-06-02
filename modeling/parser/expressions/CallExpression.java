package modeling.parser.expressions;

import java.util.List;

public class CallExpression implements Expression {
    private final Expression symbol;
    private final List<Expression> arguments;

    public CallExpression(Expression symbol, List<Expression> arguments) {
        this.symbol = symbol;
        this.arguments = arguments;
    }
}
