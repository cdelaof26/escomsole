package modeling.parser.expressions;

public record ArrayCallExpression(Expression symbol, Expression arguments) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitArrayCallExpression(this);
    }
}
