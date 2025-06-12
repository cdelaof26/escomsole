package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import modeling.NonTerminal;
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
import modeling.parser.statements.ExpressionStatement;
import modeling.parser.statements.FunctionStatement;
import modeling.parser.statements.IfStatement;
import modeling.parser.statements.LoopStatement;
import modeling.parser.statements.PrintStatement;
import modeling.parser.statements.ReturnStatement;
import modeling.parser.statements.Statement;
import modeling.parser.statements.StatementBlock;
import modeling.parser.statements.VariableStatement;

/**
 *
 * @author cristopher
 */
public class SyntacticAnalyzer {
    private static int analysisTokenIndex = 0;
    private static TokenType expected = TokenType.NONE;
    
    private static void throwError() throws IllegalStateException {
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            throw new IllegalStateException("Syntax error - missing tokens");
        
        throw new IllegalStateException(String.format("Syntax error; token #%d: %s", analysisTokenIndex, LexicalScanner.tokens.get(analysisTokenIndex)));
    }

    public static Token [] getFoundAndExpectedToken() {
        Token t;
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            t = new Token(TokenType.NONE, -1, -1);
        else
            t = LexicalScanner.tokens.get(analysisTokenIndex);

        return new Token[] {
            t, new Token(expected, t.getLine(), t.getColumn())
        };
    }

    /**
     * Retrieves the next token to be analyzed
     * @return the token
     */
    public static Token getNextToken() {
        if (analysisTokenIndex + 1 >= LexicalScanner.tokens.size())
            return new Token(TokenType.NONE, -1, -1);

        return LexicalScanner.tokens.get(analysisTokenIndex + 1);
    }
    
    /**
     * Retrieves the token being analyzed
     * @return the token
     */
    public static Token getCurrentToken() {
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            return null;
        return LexicalScanner.tokens.get(analysisTokenIndex);
    }
    
    /**
     * Retrieves the token previously analyzed
     * @return the token
     */
    public static Token getPreviousToken() {
        if (analysisTokenIndex < 1 || analysisTokenIndex - 1 >= LexicalScanner.tokens.size())
            return null;
        return LexicalScanner.tokens.get(analysisTokenIndex - 1);
    }

    public static void setAnalysisTokenIndex(int analysisTokenIndex) {
        SyntacticAnalyzer.analysisTokenIndex = analysisTokenIndex;
    }
    
    public static ArrayList<Statement> PROGRAM() throws IllegalStateException {
        return DECLARATION(null);
    }

    private static ArrayList<Statement> DECLARATION(ArrayList<Statement> statements) {
        if (statements == null)
            statements = new ArrayList<>();
        
        ArrayList<Object> firstFUN_DECL = SyntacticGrammar.first(NonTerminal.FUN_DECL);
        ArrayList<Object> firstVAR_DECL = SyntacticGrammar.first(NonTerminal.VAR_DECL);
        ArrayList<Object> firstSTATEMENT = SyntacticGrammar.first(NonTerminal.STATEMENT);
        
        if (firstFUN_DECL.contains(getCurrentTokenType())) {
            statements.add(FUN_DECL());
            DECLARATION(statements);
        } else if (firstVAR_DECL.contains(getCurrentTokenType())) {
            statements.add(VAR_DECL());
            DECLARATION(statements);
        } else if (firstSTATEMENT.contains(getCurrentTokenType())) {
            statements.add(STATEMENT());
            DECLARATION(statements);
        }
        
        // Can be epsilon
        return statements;
    }

    private static Statement FUN_DECL() {
        match(TokenType.ESC_FUN);
        
        Token name = getCurrentToken();
        match(TokenType.ESC_IDENTIFIER);
        
        match(TokenType.ESC_LEFT_PAREN);
        ArrayList<Token> params = PARAMETERS(null);
        match(TokenType.ESC_RIGHT_PAREN);
        
        StatementBlock body = BLOCK();
        
        return new FunctionStatement(name, params, body);
    }

    private static Statement VAR_DECL() {
        match(TokenType.ESC_VAR);
        
        Token name = getCurrentToken();
        match(TokenType.ESC_IDENTIFIER);
        
        Expression initializer = VAR_INIT();
        match(TokenType.ESC_SEMICOLON);
        
        return new VariableStatement(name, initializer);
    }

    private static Expression VAR_INIT() {
        if (getCurrentTokenType() == TokenType.ESC_EQUAL) {
            match(TokenType.ESC_EQUAL);
            return EXPRESSION();
        }
        
        // Can be epsilon
        return null;
    }

    private static Statement STATEMENT() {
        ArrayList<Object> firstEXPR_STMT = SyntacticGrammar.first(NonTerminal.EXPR_STMT);
        ArrayList<Object> firstFOR_STMT = SyntacticGrammar.first(NonTerminal.FOR_STMT);
        ArrayList<Object> firstIF_STMT = SyntacticGrammar.first(NonTerminal.IF_STMT);
        ArrayList<Object> firstPRINT_STMT = SyntacticGrammar.first(NonTerminal.PRINT_STMT);
        ArrayList<Object> firstRETURN_STMT = SyntacticGrammar.first(NonTerminal.RETURN_STMT);
        ArrayList<Object> firstWHILE_STMT = SyntacticGrammar.first(NonTerminal.WHILE_STMT);
        ArrayList<Object> firstBLOCK = SyntacticGrammar.first(NonTerminal.BLOCK);
        
        if (firstEXPR_STMT.contains(getCurrentTokenType()))
            return new ExpressionStatement(EXPR_STMT());
        
        if (firstFOR_STMT.contains(getCurrentTokenType()))
            return FOR_STMT();
        
        if (firstIF_STMT.contains(getCurrentTokenType()))
            return IF_STMT();
        
        if (firstPRINT_STMT.contains(getCurrentTokenType()))
            return PRINT_STMT();
        
        if (firstRETURN_STMT.contains(getCurrentTokenType()))
            return RETURN_STMT();
        
        if (firstWHILE_STMT.contains(getCurrentTokenType()))
            return WHILE_STMT();
        
        if (firstBLOCK.contains(getCurrentTokenType()))
            return BLOCK();
        
        // Cannot be epsilon
        expected = TokenType.STATEMENT;
        throwError();
        return null;
    }

    private static Expression EXPR_STMT() {
        Expression expression = EXPRESSION();
        match(TokenType.ESC_SEMICOLON);
        
        return expression;
    }

    private static Statement FOR_STMT() {
        match(TokenType.ESC_FOR);
        match(TokenType.ESC_LEFT_PAREN);
        Statement initializer = FOR_STMT_INIT();
        Expression condition = FOR_STMT_COND();
        Statement increment = FOR_STMT_INC();
        match(TokenType.ESC_RIGHT_PAREN);
        Statement body = STATEMENT();
        
        if (increment != null)
            body = new StatementBlock(Arrays.asList(body, increment));
                
        if (condition == null)
            condition = new LiteralExpression(true);
        
        LoopStatement loopStatement = new LoopStatement(condition, body);
        
        if (initializer == null)
            return loopStatement;
        
        return new StatementBlock(Arrays.asList(initializer, loopStatement));
    }

    private static Statement FOR_STMT_INIT() {
        ArrayList<Object> firstVAR_DECL = SyntacticGrammar.first(NonTerminal.VAR_DECL);
        ArrayList<Object> firstEXPR_STMT = SyntacticGrammar.first(NonTerminal.EXPR_STMT);
        
        if (firstVAR_DECL.contains(getCurrentTokenType()))
            return VAR_DECL();
        
        if (firstEXPR_STMT.contains(getCurrentTokenType()))
            return new ExpressionStatement(EXPR_STMT());
        
        match(TokenType.ESC_SEMICOLON);
        return null;
    }

    private static Expression FOR_STMT_COND() {
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        
        if (firstEXPRESSION.contains(getCurrentTokenType())) {
            Expression expression = EXPRESSION();
            match(TokenType.ESC_SEMICOLON);
            return expression;
        }

        match(TokenType.ESC_SEMICOLON);
        return null;
    }

    private static Statement FOR_STMT_INC() {
        ArrayList<Object> firstFOR_STMT_INC = SyntacticGrammar.first(NonTerminal.FOR_STMT_INC);
        if (firstFOR_STMT_INC.contains(getCurrentTokenType()))
            return new ExpressionStatement(EXPRESSION());
        
        // Can be epsilon
        return null;
    }

    private static Statement IF_STMT() {
        match(TokenType.ESC_IF);
        
        match(TokenType.ESC_LEFT_PAREN);
        Expression condition = EXPRESSION();
        match(TokenType.ESC_RIGHT_PAREN);
        
        Statement thenBranch = STATEMENT();
        Statement elseBranch = ELSE_STATEMENT();
        
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private static Statement ELSE_STATEMENT() {
        if (getCurrentTokenType() == TokenType.ESC_ELSE) {
            match(TokenType.ESC_ELSE);
            return STATEMENT();
        }
        
        // Can be epsilon
        return null;
    }

    private static Statement PRINT_STMT() {
        match(TokenType.ESC_PRINT);
        Expression expression = EXPRESSION();
        match(TokenType.ESC_SEMICOLON);
        
        return new PrintStatement(expression);
    }

    private static Statement RETURN_STMT() {
        match(TokenType.ESC_RETURN);
        Expression value = RETURN_EXP_OPC();
        match(TokenType.ESC_SEMICOLON);
        
        return new ReturnStatement(value);
    }

    private static Expression RETURN_EXP_OPC() {
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        if (firstEXPRESSION.contains(getCurrentTokenType()))
            return EXPRESSION();
        
        // Can be epsilon
        return null;
    }

    private static Statement WHILE_STMT() {
        match(TokenType.ESC_WHILE);
        
        match(TokenType.ESC_LEFT_PAREN);
        Expression condition = EXPRESSION();
        match(TokenType.ESC_RIGHT_PAREN);
        
        Statement body = STATEMENT();
        
        return new LoopStatement(condition, body);
    }

    private static StatementBlock BLOCK() {
        match(TokenType.ESC_LEFT_BRACE);
        ArrayList<Statement> statements = DECLARATION(null);
        match(TokenType.ESC_RIGHT_BRACE);
        
        return new StatementBlock(statements);
    }

    private static Expression EXPRESSION() {
        return ASSIGNMENT();
    }
    
    
    private static Expression ASSIGNMENT() {
        Expression leftExpression = CONDITIONAL();
        
        Token name = getPreviousToken();
        Token operator = getCurrentToken();
        Expression rightExpression = ASSIGNMENT_OPC();
        if (rightExpression == null)
            return leftExpression;
        
        return new AssignmentExpression(name, operator, rightExpression);
    }
    
    private static Expression ASSIGNMENT_OPC() {
        switch (getCurrentTokenType()) {
            case ESC_EQUAL:
            case ESC_STAR_EQUAL:
            case ESC_SLASH_EQUAL:
            case ESC_PLUS_EQUAL:
            case ESC_MINUS_EQUAL:
                match(getCurrentTokenType());
            return EXPRESSION();
        }
        
        // Can be epsilon
        return null;
    }
    
    private static Expression CONDITIONAL() {
        Expression leftExpression = LOGIC_OR();
        Expression rightExpression = CONDITIONAL_P(leftExpression);
        if (rightExpression == null)
            return leftExpression;
        
        return rightExpression;
    }
   
    private static Expression CONDITIONAL_P(Expression leftExpression) {
        if (getCurrentTokenType() == TokenType.ESC_QUESTION_MARK) {
            match(getCurrentTokenType());
            Expression thenBranch = CONDITIONAL();
            match(TokenType.ESC_COLON);
            Expression elseBranch = CONDITIONAL();
            
            return new TernaryExpression(leftExpression, thenBranch, elseBranch);
        }
        
        // Can be epslion
        return null;
    }

    private static Expression LOGIC_OR() {
        Expression leftExpression = LOGIC_AND();
        
        Token operator = getCurrentToken();
        Expression rightExpression = LOGIC_OR_P();
        if (rightExpression == null)
            return leftExpression;
        
        return new LogicalExpression(leftExpression, operator, rightExpression);
    }

    private static Expression LOGIC_OR_P() {
        if (getCurrentTokenType() == TokenType.ESC_OR) {
            match(TokenType.ESC_OR);
            return LOGIC_OR();
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression LOGIC_AND() {
        Expression leftExpression = EQUALITY();
        
        Token operator = getCurrentToken();
        Expression rightExpression = LOGIC_AND_P();
        if (rightExpression == null)
            return leftExpression;
        
        return new LogicalExpression(leftExpression, operator, rightExpression);
    }

    private static Expression LOGIC_AND_P() {
        if (getCurrentTokenType() == TokenType.ESC_AND) {
            match(TokenType.ESC_AND);
            return LOGIC_AND();
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression EQUALITY() {
        Expression leftExpression = COMPARISON();
        
        Token operator = getCurrentToken();
        Expression rightExpression = EQUALITY_P();
        if (rightExpression == null)
            return leftExpression;
        
        return new RelationalExpression(leftExpression, operator, rightExpression);
    }

    private static Expression EQUALITY_P() {
        switch (getCurrentTokenType()) {
            case ESC_NOT_EQUAL:
            case ESC_EQUAL_EQUAL:
                match(getCurrentTokenType());
            return EQUALITY();
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression COMPARISON() {
        Expression leftExpression = TERM();
        
        Token operator = getCurrentToken();
        Expression rightExpression = COMPARISON_P();
        if (rightExpression == null)
            return leftExpression;
        
        return new RelationalExpression(leftExpression, operator, rightExpression);
    }

    private static Expression COMPARISON_P() {
        switch (getCurrentTokenType()) {
            case ESC_GREATER:
            case ESC_GREATER_EQUAL:
            case ESC_LESS:
            case ESC_LESS_EQUAL:
                match(getCurrentTokenType());
            return COMPARISON();
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression TERM() {
        Expression leftExpression = FACTOR();
        
        Token operator = getCurrentToken();
        Expression rightExpression = TERM_P(leftExpression);
        
        if (rightExpression instanceof UnaryExpression)
            return rightExpression;
        
        if (rightExpression == null)
            return leftExpression;
        
        return new ArithmeticExpression(leftExpression, operator, rightExpression);
    }

    private static Expression TERM_P(Expression leftExpression) {
        Token t = getCurrentToken();
        if (t == null)
            throwError();
        
        switch (t.getType()) {
            case ESC_MINUS:
            case ESC_PLUS:
                match(t.getType());
            return TERM();
            
            case ESC_MINUS_MINUS:
            case ESC_PLUS_PLUS:
                // In text, it'll be scanned as identifier++
                // however, will be converted into ++identifier
                match(t.getType());
            return new UnaryExpression(t, leftExpression);
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression FACTOR() {
        Expression leftExpression = UNARY();
        
        Token operator = getCurrentToken();
        Expression rightExpression = FACTOR_P();
        if (rightExpression == null)
            return leftExpression;
        
        return new ArithmeticExpression(leftExpression, operator, rightExpression);
    }

    private static Expression FACTOR_P() {
        switch (getCurrentTokenType()) {
            case ESC_SLASH:
            case ESC_STAR:
                match(getCurrentTokenType());
            return FACTOR();
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression UNARY() {
        switch (getCurrentTokenType()) {
            case ESC_NOT:
            case ESC_MINUS:
                match(getCurrentTokenType());
            return UNARY();
            
            default:
            return CALL();
        }
    }

    private static Expression CALL() {
        Expression leftExpression = PRIMARY();
        Expression rightExpression = CALL_P(leftExpression);
        if (rightExpression == null)
            return leftExpression;
        
        return rightExpression;
    }

    private static Expression CALL_P(Expression rightExpression) {
        switch (getCurrentTokenType()) {
            case ESC_LEFT_PAREN:
                TokenType previous = getPreviousToken().getType();
                if (previous != TokenType.ESC_IDENTIFIER) {
                    // Only identifiers are callable
                    analysisTokenIndex--;
                    expected = TokenType.ESC_IDENTIFIER;
                    throwError();
                }
                
                match(getCurrentTokenType());
                ArrayList<Expression>  arguments = ARGUMENTS(null);
                match(TokenType.ESC_RIGHT_PAREN);
            return new CallExpression(rightExpression, arguments);
            
//            case ESC_QUESTION_MARK:
//                match(getCurrentTokenType());
//                Statement thenBranch = STATEMENT();
//                match(TokenType.ESC_COLON);
//                Statement elseBranch = STATEMENT();
//            return new TernaryExpression(rightExpression, thenBranch, elseBranch);
            
            case ESC_LEFT_BRACKET:
                match(getCurrentTokenType());
                Expression expression = EXPRESSION();
                match(TokenType.ESC_RIGHT_BRACKET);
            return new ArrayCallExpression(rightExpression, expression);
                
//                ArrayList<Object> firstARRAY = SyntacticGrammar.first(NonTerminal.ARRAY);
//                if (firstARRAY.contains(getCurrentTokenType()))
//                    return ARRAY();
            
        }
        
        // Can be epsilon
        return null;
    }

    private static Expression PRIMARY() {
        Token t;
        
        switch (getCurrentTokenType()) {
            case ESC_TRUE:
            case ESC_FALSE:
            case ESC_NULL:
            case ESC_NUMBER:
            case ESC_FLOATING_NUMBER:
            case ESC_DOUBLE_NUMBER:
            case ESC_STRING:
                t = getCurrentToken();
                match(t.getType());
            return new LiteralExpression(t.getLiteral());
            
            case ESC_IDENTIFIER:
                t = getCurrentToken();
                match(t.getType());
            return new LiteralExpression(new Identifier((String) t.getLiteral()));
            
            case ESC_LEFT_PAREN:
                match(getCurrentTokenType());
                Expression expression = EXPRESSION();
                match(TokenType.ESC_RIGHT_PAREN);
            return new GroupingExpression(expression);
            
            default:
                ArrayList<Object> firstARRAY = SyntacticGrammar.first(NonTerminal.ARRAY);
                if (firstARRAY.contains(getCurrentTokenType()))
                    return ARRAY();
                
                expected = TokenType.NONE;
                throwError();
            return null;
        }
    }

    private static Expression ARRAY() {
        match(TokenType.ESC_LEFT_BRACKET);
        ArrayList<Expression> arguments = ARGUMENTS(null);
        match(TokenType.ESC_RIGHT_BRACKET);
        
        return new LiteralExpression(arguments);
    }

    private static ArrayList<Token> PARAMETERS(ArrayList<Token> parameters) {
        if (parameters == null)
            parameters = new ArrayList<>();
        
        Token identifier = getCurrentToken();
        if (identifier.getType() == TokenType.ESC_IDENTIFIER) {
            parameters.add(identifier);
            match(identifier.getType());
            
            PARAMETERS_P(parameters);
        }
        
        // Can be epsilon
        return parameters;
    }

    private static void PARAMETERS_P(ArrayList<Token> parameters) {
        if (getCurrentTokenType() == TokenType.ESC_COMMA) {
            match(getCurrentTokenType());
            
            parameters.add(getCurrentToken());
            match(TokenType.ESC_IDENTIFIER);
            
            PARAMETERS_P(parameters);
        }
        
        // Can be epsilon
    }

    private static ArrayList<Expression> ARGUMENTS(ArrayList<Expression> arguments) {
        if (arguments == null)
            arguments = new ArrayList<>();
        
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        if (firstEXPRESSION.contains(getCurrentTokenType())) {
            arguments.add(EXPRESSION());
            ARGUMENTS_P(arguments);
        }

        // Can be epsilon
        return arguments;
    }

    private static void ARGUMENTS_P(ArrayList<Expression> arguments) {
        if (getCurrentTokenType() == TokenType.ESC_COMMA) {
            match(getCurrentTokenType());
            arguments.add(EXPRESSION());
            ARGUMENTS_P(arguments);
        }
        
        // Can be epslion
    }
    
    /**
     * Retrieves the TokenType of the token being analyzed
     * @return the token
     */
    public static TokenType getCurrentTokenType() {
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            return TokenType.NONE;
        return LexicalScanner.tokens.get(analysisTokenIndex).getType();
    }
    
    /**
     * Given a t TokenType, this function checks if the current token is equal
     * @param t the TokenType to compare current TokenType against
     * @throws IllegalStateException if the tokens don't match
     */
    private static void match(TokenType t) throws IllegalStateException {
        if (t == getCurrentTokenType()) {
            analysisTokenIndex++;
            return;
        }
        
        expected = t;
        
        throwError();
    }
}
