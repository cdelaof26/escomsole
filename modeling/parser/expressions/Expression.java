package modeling.parser.expressions;

public interface Expression {
    public <T> T accept(VisitorExpression<T> visitor);
}
