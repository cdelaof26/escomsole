package modeling.parser.statements;

import java.util.List;

public class StatementBlock implements Statement {
    private final List<Statement> statements;

    public StatementBlock(List<Statement> statements) {
        this.statements = statements;
    }
}
