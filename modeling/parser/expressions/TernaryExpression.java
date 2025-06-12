package modeling.parser.expressions;

/**
 *
 * @author cristopher
 */
public record TernaryExpression(Expression condition, Expression thenBranch, Expression elseBranch) implements Expression {
    @Override
    public <T> T accept(VisitorExpression<T> visitor) {
        return visitor.visitTernaryExpression(this);
    }
}
