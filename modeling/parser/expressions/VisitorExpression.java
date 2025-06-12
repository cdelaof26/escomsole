package modeling.parser.expressions;

public interface VisitorExpression<T> {
    public T visitArithmeticExpression(ArithmeticExpression expression);
    public T visitArrayCallExpression(ArrayCallExpression expression);
    public T visitAssignmentExpression(AssignmentExpression expression);
    public T visitCallExpression(CallExpression expression);
    public T visitGroupingExpression(GroupingExpression expression);
    public T visitLiteralExpression(LiteralExpression expression);
    public T visitLogicalExpression(LogicalExpression expression);
    public T visitRelationalExpression(RelationalExpression expression);
    public T visitTernaryExpression(TernaryExpression expression);
    public T visitUnaryExpression(UnaryExpression expression);
    public T visitVariableExpression(VariableExpression expression);
    
//    T visitGetExpression(GetExpression expression);
//    T visitSetExpression(SetExpression expression);
//    T visitSuperExpression(SuperExpression expression);
//    T visitThisExpression(ThisExpression expression);
}
