package interpreter.core;

import modeling.parser.statements.VisitorStatement;
import modeling.parser.expressions.VisitorExpression;
import interpreter.exception.RuntimeError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modeling.Token;
import modeling.TokenType;
import modeling.parser.expressions.ArithmeticExpression;
import modeling.parser.expressions.ArrayCallExpression;
import modeling.parser.expressions.AssignmentExpression;
import modeling.parser.expressions.CallExpression;
import modeling.parser.expressions.Expression;
import modeling.parser.expressions.GroupingExpression;
import modeling.parser.expressions.LiteralExpression;
import modeling.parser.expressions.Identifier;
import modeling.parser.expressions.LogicalExpression;
import modeling.parser.expressions.RelationalExpression;
import modeling.parser.expressions.TernaryExpression;
import modeling.parser.expressions.UnaryExpression;
import modeling.parser.expressions.VariableExpression;
import modeling.parser.statements.ExpressionStatement;
import modeling.parser.statements.FunctionStatement;
import modeling.parser.statements.IfStatement;
import modeling.parser.statements.LoopStatement;
import modeling.parser.statements.PrintStatement;
import modeling.parser.statements.ReturnStatement;
import modeling.parser.statements.Statement;
import modeling.parser.statements.StatementBlock;
import modeling.parser.statements.VariableStatement;

public class VisitorImplementationInterpreter implements VisitorExpression<Object>, VisitorStatement<Void> {
    private final Environment globals;
    private Environment environment;
    private final Map<Expression, Integer> locals = new HashMap<>();

    public VisitorImplementationInterpreter() {
        this.globals = new Environment();
        this.environment = globals;
    }
    
    public VisitorImplementationInterpreter(Environment environment) {
        this.globals = environment;
        this.environment = globals;
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }
    
    public Void evaluate(Statement statement) {
        statement.accept(this);
        return null;
    }

    @Override
    public Object visitArithmeticExpression(ArithmeticExpression expression) {
        Object leftResult = evaluate(expression.leftExpression());
        if (leftResult instanceof Identifier left)
            leftResult = environment.get(left.value());
        
        if (!(leftResult instanceof Number left))
            throw new RuntimeError(leftResult, "Operand must be numeric");
        
        
        Object rightResult = evaluate(expression.rightExpression());
        if (rightResult instanceof Identifier right)
            rightResult = environment.get(right.value());
        
        if (!(rightResult instanceof Number right))
            throw new RuntimeError(rightResult, "Operand must be numeric");
        
        
        TokenType type = expression.operator().getType();
        
        Object result;
        if (right instanceof Double || left instanceof Double) {
            result = type == TokenType.ESC_MINUS ? left.doubleValue() - right.doubleValue() :
                    type == TokenType.ESC_PLUS ? left.doubleValue() + right.doubleValue() :
                    type == TokenType.ESC_SLASH ? left.doubleValue() / right.doubleValue() :
                    type == TokenType.ESC_STAR ? left.doubleValue() * right.doubleValue() : null;
            
        } else if (right instanceof Float || left instanceof Float) {
            result = type == TokenType.ESC_MINUS ? left.floatValue()- right.floatValue() :
                    type == TokenType.ESC_PLUS ? left.floatValue() + right.floatValue() :
                    type == TokenType.ESC_SLASH ? left.floatValue() / right.floatValue() :
                    type == TokenType.ESC_STAR ? left.floatValue() * right.floatValue() : null;
        } else {
            result = type == TokenType.ESC_MINUS ? left.intValue() - right.intValue() :
                    type == TokenType.ESC_PLUS ? left.intValue() + right.intValue() :
                    type == TokenType.ESC_SLASH ? left.intValue() / right.intValue() :
                    type == TokenType.ESC_STAR ? left.intValue() * right.intValue() : null;
        }
        
        if (result != null)
            return result;
        
        throw new RuntimeError(
            expression.operator(), "Operator cannot be used to perform arithmetic operations"
        );
    }

    @Override
    public Object visitArrayCallExpression(ArrayCallExpression expression) {
        Object symbolResult = evaluate(expression.symbol());
        
        if (!(symbolResult instanceof Identifier symbolLSE))
            throw new RuntimeError(expression.symbol(), "Expression is not callable");
        
        Object value = environment.get(symbolLSE.value());
        if (!(value instanceof List array))
            throw new RuntimeError(value, "Object is not an array");
        
        
        Object argument = evaluate(expression.arguments());
        if (argument instanceof Integer index) {
            if (array.size() < index)
                return null;
            
            if (array.get(index) instanceof LiteralExpression le)
                return le.value();
            
            return array.get(index);
        }
        
        if (!(argument instanceof Identifier argumentLSE))
            throw new RuntimeError(argument, "Array index must be an identifier or Integer");
        
        if (environment.get(argumentLSE.value()) instanceof Integer index) {
            if (array.size() < index)
                return null;

            if (array.get(index) instanceof LiteralExpression le)
                return le.value();
            
            return array.get(index);
        }

        throw new RuntimeError(argumentLSE.value(), "Identifier is not an Integer");
    }

    @Override
    public Object visitAssignmentExpression(AssignmentExpression expression) {
        // Assignment doesn't include reserved 'var' keyword
        
        if (expression.name().getType() != TokenType.ESC_IDENTIFIER)
            throw new RuntimeError(expression.name(), "Object is not assignable");
        
        String identifier = expression.name().getLexeme();
        Object result = evaluate(expression.value());
        if (result instanceof Identifier id)
            result = environment.get(id.value());
        
        if (expression.operator().getType() == TokenType.ESC_EQUAL) {
            environment.assign(identifier, result);
            return null;
        }
        
        Token t;
        switch (expression.operator().getType()) {
            case ESC_MINUS_EQUAL:
                t = new Token(TokenType.ESC_MINUS, -1, -1);
            break;
            case ESC_PLUS_EQUAL:
                t = new Token(TokenType.ESC_PLUS, -1, -1);
            break;
            case ESC_SLASH_EQUAL:
                t = new Token(TokenType.ESC_SLASH, -1, -1);
            break;
            case ESC_STAR_EQUAL:
                t = new Token(TokenType.ESC_STAR, -1, -1);
            break;
            
            default:
            throw new RuntimeError(expression.operator().getType(), "Operator is not arithmetic");
        }
        
        environment.assign(identifier, 
            evaluate(new ArithmeticExpression(
                    new LiteralExpression(new Identifier(identifier)), 
                    t, new LiteralExpression(result))
            )
        );
        
        return null;
    }

    @Override
    public Object visitCallExpression(CallExpression expression) {
        Object symbolResult = evaluate(expression.symbol());
        
        if (!(symbolResult instanceof Identifier identifier))
            throw new RuntimeError(symbolResult, "Object is not callable");
        
        Object [] data = environment.getFunction(identifier.value());
        List<Token> parameters = (List<Token>) data[0];
        StatementBlock body = (StatementBlock) data[1];
        
        int args = expression.arguments().size();
        int params = parameters.size();
        if (args != params)
            throw new RuntimeError(
                expression.arguments(), 
                String.format("%s expected %d argument(s) but %d were given", identifier.value(), params, args)
            );
        
        List<VariableStatement> arguments = new ArrayList<>();
        for (int i = 0; i < args; i++)
            arguments.add(new VariableStatement(parameters.get(i), expression.arguments().get(i)));
        
        // System.out.println("arguments = " + arguments);
        body.setInitVariables(arguments);
        // body.setAutoCloseScope(false);
        
        evaluate(body);
        
        return globals.getReturnValue();
    }

    @Override
    public Object visitGroupingExpression(GroupingExpression expression) {
        return evaluate(expression.expression());
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.value();
    }

    @Override
    public Object visitLogicalExpression(LogicalExpression expression) {
        Object leftResult = evaluate(expression.leftExpression());
        if (leftResult instanceof Identifier left)
            leftResult = environment.get(left.value());
        if (leftResult instanceof String left)
            leftResult = !left.isEmpty();
        
        if (!(leftResult instanceof Boolean left))
            throw new RuntimeError(leftResult, "Left operand must be a bool");


        Object rightResult = evaluate(expression.rightExpression());
        if (rightResult instanceof Identifier right)
            rightResult = environment.get(right.value());
        if (rightResult instanceof String right)
            rightResult = !right.isEmpty();
        
        if (!(rightResult instanceof Boolean right))
            throw new RuntimeError(rightResult, "Right operand must be a bool");
        
        
        if (expression.operator().getType() == TokenType.ESC_AND)
            return left && right;
        
        if (expression.operator().getType() == TokenType.ESC_OR)
            return left || right;
        
        throw new RuntimeError(expression, "Operator is not logical");
    }

    @Override
    public Object visitRelationalExpression(RelationalExpression expression) {
        Object leftResult = evaluate(expression.leftExpression());
        if (leftResult instanceof Identifier left)
            leftResult = environment.get(left.value());
        if (!(leftResult instanceof Number))
            throw new RuntimeError(leftResult, "Left operand must be numeric");
        
        
        Object rightResult = evaluate(expression.rightExpression());
        if (rightResult instanceof Identifier right)
            rightResult = environment.get(right.value());
        if (!(rightResult instanceof Number))
            throw new RuntimeError(rightResult, "Right operand must be numeric");
        
        
        double left = ((Number) leftResult).doubleValue();
        double right = ((Number) rightResult).doubleValue();
        
        if (expression.operator().getType() == TokenType.ESC_NOT_EQUAL)
            return left != right;
        if (expression.operator().getType() == TokenType.ESC_EQUAL_EQUAL)
            return left == right;
        if (expression.operator().getType() == TokenType.ESC_GREATER)
            return left > right;
        if (expression.operator().getType() == TokenType.ESC_GREATER_EQUAL)
            return left >= right;
        if (expression.operator().getType() == TokenType.ESC_LESS)
            return left < right;
        if (expression.operator().getType() == TokenType.ESC_LESS_EQUAL)
            return left <= right;
        
        throw new RuntimeError(expression, "Operator is not logical");
    }

    @Override
    public Object visitTernaryExpression(TernaryExpression expression) {
        Object conditionResult = evaluate(expression.condition());
        if (conditionResult instanceof Identifier identifier)
            conditionResult = environment.get(identifier.value());
        if (conditionResult instanceof String result)
            conditionResult = !result.isEmpty();
            
        if (!(conditionResult instanceof Boolean condition))
            throw new RuntimeError(conditionResult, "Condition is not a bool");
        
        return condition ? evaluate(expression.thenBranch()) : evaluate(expression.elseBranch());
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression expression) {
        Object result = evaluate(expression.rightExpression());
        TokenType operator = expression.operator().getType();
        
        boolean isAssignment = operator == TokenType.ESC_PLUS_PLUS || operator == TokenType.ESC_MINUS_MINUS;
        
        if (isAssignment) {
            if (!(result instanceof Identifier r))
                throw new RuntimeError(result, "Object is not assignable");
//            operator == TokenType.ESC_PLUS_PLUS || operator == TokenType.ESC_MINUS_MINUS

            return evaluate(new AssignmentExpression(
                    new Token(TokenType.ESC_IDENTIFIER, r.value(), r, -1, -1), 
                    new Token(operator == TokenType.ESC_PLUS_PLUS ? TokenType.ESC_PLUS_EQUAL : TokenType.ESC_MINUS_EQUAL, -1, -1), 
                    new LiteralExpression(1)
            ));
        }
        
        if (result instanceof Identifier r)
            result = environment.get(r.value());
        
        if (result instanceof Number) {
            if (operator != TokenType.ESC_MINUS)
                throw new RuntimeError(expression.operator(), "Incompatible unary operator");
            
            if (result instanceof Integer num)
                return -num;
            if (result instanceof Float num)
                return -num;
            if (result instanceof Double num)
                return -num;
            
            // Might point of errors if other numeric values are introduced
        }
        
        if (result instanceof Boolean b) {
            if (operator != TokenType.ESC_NOT)
                throw new RuntimeError(expression.operator(), "Incompatible unary operator");
            
            return !b;
        }

        throw new RuntimeError(expression.operator(), "Operand must be a number or a bool");
    }

    @Override
    public Object visitVariableExpression(VariableExpression expression) {
        // No idea what's this for
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visitStatementBlock(StatementBlock statement) {
        environment = new Environment(environment);
        
        if (statement.hasInitVariables()) {
            for (VariableStatement vs : statement.getInitVariables())
                evaluate(vs);
            
            // System.out.println("environment.getValues() = " + environment.getValues());
        }
        
        for (Statement s : statement.statements()) {
            evaluate(s);
            
//            System.out.println("globals.getBreakStack() = " + globals.getBreakStack());
            if (globals.shouldBreakStack()) {
                statement.setAutoCloseScope(false);
                break;
            }
            
            if (s instanceof ReturnStatement) {
                statement.setAutoCloseScope(false);
                globals.incrBreakStack();
//                System.out.println("break " + s);
                break;
            }
        }
        
        if (statement.isAutoCloseScopeActive())
            environment = environment.getEnclosing();
        
//        System.out.println("exit " + statement.getInitVariables());
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.expression());
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        environment.defineFunction(
            (String) statement.name().getLiteral(), 
            new Object[] {
                statement.params(),
                statement.body()
            }
        );
        
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {
        Object conditionResult = evaluate(statement.condition());
        if (conditionResult instanceof Identifier identifier)
            conditionResult = environment.get(identifier.value());
        if (conditionResult instanceof String result)
            conditionResult = !result.isEmpty();
            
        if (!(conditionResult instanceof Boolean condition))
            throw new RuntimeError(conditionResult, "Condition is not a bool");
        
        return condition ? evaluate(statement.thenBranch()) : 
                statement.elseBranch() != null ? evaluate(statement.elseBranch()) : null;
    }

    @Override
    public Void visitLoopStatement(LoopStatement statement) {
        while (true) {
            Object conditionResult = evaluate(statement.condition());
            if (conditionResult instanceof Identifier identifier)
                conditionResult = environment.get(identifier.value());
            if (conditionResult instanceof String result)
                conditionResult = !result.isEmpty();

            if (!(conditionResult instanceof Boolean condition))
                throw new RuntimeError(conditionResult, "Condition is not a bool");

            if (!condition)
                break;
            
            evaluate(statement.body());
        }
        
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        Object result = evaluate(statement.expression());
        if (result instanceof Identifier id)
            System.out.println(environment.get(id.value()));
        else
            System.out.println(result);
        
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        Object result = evaluate(statement.value());
        if (result instanceof Identifier identifier)
            result = environment.get(identifier.value());
        
        globals.addReturnValue(result);
        
        // Close environment
//        System.out.println("environment.getValues() = " + environment.getValues());
        environment = environment.getEnclosing();
//        System.out.println("globals.returnValues() = " + globals.getReturnValues());
        
        return null;
    }

    @Override
    public Void visitVariableStatement(VariableStatement statement) {
        Object ini = statement.initializer() == null ? null : evaluate(statement.initializer());
        
        environment.define(
            (String) statement.name().getLiteral(), 
            ini
        );
        
        return null;
    }
}
