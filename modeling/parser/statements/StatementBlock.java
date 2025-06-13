package modeling.parser.statements;

import java.util.List;

public class StatementBlock implements Statement {
    private List<Statement> statements;
    private List<VariableStatement> initVariables = null;
    private boolean autoCloseScope = true;

    public StatementBlock(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> statements() {
        return statements;
    }

    public void setInitVariables(List<VariableStatement> initVariables) {
        this.initVariables = initVariables;
    }
    
    public boolean hasInitVariables() {
        return this.initVariables != null;
    }

    public List<VariableStatement> getInitVariables() {
        return initVariables;
    }

    public void setAutoCloseScope(boolean autoCloseScope) {
        this.autoCloseScope = autoCloseScope;
    }

    public boolean isAutoCloseScopeActive() {
        return autoCloseScope;
    }
    
    @Override
    public <T> T accept(VisitorStatement<T> visitor) {
        return visitor.visitStatementBlock(this);
    }
}
