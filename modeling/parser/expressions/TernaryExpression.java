package modeling.parser.expressions;

/**
 *
 * @author cristopher
 */
public class TernaryExpression implements Expression {
    private final Expression condition;
    private final Expression thenBranch;
    private final Expression elseBranch;

    public TernaryExpression(Expression condition, Expression thenBranch, Expression elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
