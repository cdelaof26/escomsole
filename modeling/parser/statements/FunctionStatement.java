package modeling.parser.statements;

import java.util.List;
import modeling.Token;

public class FunctionStatement implements Statement {
    private final Token name;
    private final List<Token> params;
    private final StatementBlock body;

    public FunctionStatement(Token name, List<Token> params, StatementBlock body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
}
