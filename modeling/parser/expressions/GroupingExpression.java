package modeling.parser.expressions;

public record GroupingExpression(Expression expression) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitGroupingExpression(this);
    }
}
