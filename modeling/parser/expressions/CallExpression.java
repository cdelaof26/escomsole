package modeling.parser.expressions;

import java.util.List;

public record CallExpression(Expression symbol, List<Expression> arguments) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitCallExpression(this);
    }
}
