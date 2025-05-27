package modeling.parser.statements;

import java.util.List;
import modeling.Token;

public class FunctionStatement implements Statement {
    final Token name;
    final List<Token> params;
    final StatementBlock body;

    FunctionStatement(Token name, List<Token> params, StatementBlock body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
}
