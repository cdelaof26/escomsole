package modeling.parser.statements;

import java.util.List;
import modeling.Token;

public record FunctionStatement(Token name, List<Token> params, StatementBlock body) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitFunctionStatement(this);
    }
}
