package modeling.parser.statements;

public interface VisitorStatement<T> {
    public T visitExpressionStatement(ExpressionStatement statement);
    public T visitFunctionStatement(FunctionStatement statement);
    public T visitIfStatement(IfStatement statement);
    public T visitLoopStatement(LoopStatement statement);
    public T visitPrintStatement(PrintStatement statement);
    public T visitReturnStatement(ReturnStatement statement);
    public T visitStatementBlock(StatementBlock statement);
    public T visitVariableStatement(VariableStatement statement);
    
//    T visitClassStatement(ClassStatement statement);
}
