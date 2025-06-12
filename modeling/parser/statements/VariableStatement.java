package modeling.parser.statements;

import modeling.parser.expressions.Expression;
import modeling.Token;

public record VariableStatement(Token name, Expression initializer) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitVariableStatement(this);
    }
}
