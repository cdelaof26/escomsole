package modeling.parser.statements;

import java.util.List;

public record StatementBlock(List<Statement> statements) implements Statement {
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitStatementBlock(this);
    }
}
