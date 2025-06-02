package modeling.parser.expressions;

public class GroupingExpression implements Expression {
    private final Expression expression;

    public GroupingExpression(Expression expression) {
        this.expression = expression;
    }
}
