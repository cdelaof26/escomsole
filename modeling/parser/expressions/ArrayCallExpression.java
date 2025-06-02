package modeling.parser.expressions;

public class ArrayCallExpression implements Expression {
    private final Expression symbol;
    private final Expression arguments;

    public ArrayCallExpression(Expression symbol, Expression arguments) {
        this.symbol = symbol;
        this.arguments = arguments;
    }
}
