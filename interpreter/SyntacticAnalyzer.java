package interpreter;

import java.util.ArrayList;
import java.util.Arrays;

import parser.SyntacticGrammar;
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
import modeling.parser.expressions.LogicalExpression;
import modeling.parser.expressions.RelationalExpression;
import modeling.parser.expressions.TernaryExpression;
import modeling.parser.statements.ExpressionStatement;
import modeling.parser.statements.FunctionStatement;
import modeling.parser.statements.IfStatement;
import modeling.parser.statements.LoopStatement;
import modeling.parser.statements.PrintStatement;
import modeling.parser.statements.ReturnStatement;
import modeling.parser.statements.Statement;
import modeling.parser.statements.StatementBlock;
import modeling.parser.statements.VariableStatement;

public class SyntacticAnalyzer {
  private static int analysisTokenIndex = 0;
  private static TokenType expected = TokenType.NONE;

  private static void throwError() throws IllegalStateException {
    if (analysisTokenIndex >= LexicalScanner.tokens.size())
      throw new IllegalStateException("Syntax error - missing tokens");

    throw new IllegalStateException(String.format("Syntax error: token #%d: %s", analysisTokenIndex,
        LexicalScanner.tokens.get(analysisTokenIndex)));
  }

  public static Token[] getFoundAndExpectedToken() {
    Token t;

    if (analysisTokenIndex >= LexicalScanner.tokens.size())
      t = new Token(TokenType.NONE, -1, -1);
    else
      t = LexicalScanner.tokens.get(analysisTokenIndex);

    return new Token[] {
        t, new Token(expected, t.getLine(), t.getColumn())
    };
  }
  
  public static Token getCurrentToken() {
    if (analysisTokenIndex >= LexicalScanner.tokens.size())
      return null;

    return LexicalScanner.tokens.get(analysisTokenIndex);
  }

  public static TokenType getCurrentTokenType() {
    if (analysisTokenIndex >= LexicalScanner.tokens.size())
      return TokenType.NONE;

    return LexicalScanner.tokens.get(analysisTokenIndex).getType();
  }
  
  public static Token getPreviousToken() {
    if (analysisTokenIndex < 1 || analysisTokenIndex - 1 >= LexicalScanner.tokens.size())
      return null;

    return LexicalScanner.tokens.get(analysisTokenIndex - 1);
  }

  public static void match(TokenType t) throws IllegalStateException {
    if (t == getCurrentTokenType()) {
      analysisTokenIndex++;
      return;
    }

    expected = t;
    throwError();
  }

  public static boolean getFirstArray(NonTerminal n) {
    ArrayList<Object> firstG = SyntacticGrammar.first(n);

    Token token = getCurrentToken();
    if (token == null)
      return false;

    return firstG.contains(token.getType()); 
  }
  
  public static boolean parse() {
    try {
      PROGRAM();
      System.out.println("Programa válido");
      return true;
    } catch (Exception e) {
      System.err.println("Error sintáctico: " + e.getMessage());
      return false;
    }
  }


  public static ArrayList<Statement> PROGRAM() {
    // System.out.println("Program");
    ArrayList<Statement> statements = DECLARATION();
    return statements;
  }

  public static ArrayList<Statement>  DECLARATION() {
    Token token = getCurrentToken();

    ArrayList<Statement> statements = new ArrayList<>();

    // .out.println("Declaration" + token.getType());
    // System.out.println(SyntacticGrammar.first(NonTerminal.STATEMENT));
    // System.out.println(getFirstArray(NonTerminal.STATEMENT));

    if (token == null || token.getType() == TokenType.ESC_EOF) {
      // Epsilon: fin de las declaraciones, no hacer nada
      return statements;
    }
    
    if (getFirstArray(NonTerminal.FUN_DECL)) {
      Statement statement = FUN_DECL();
      ArrayList<Statement> a = DECLARATION();
      statements.add(statement);
      statements.addAll(a);
    } else if (getFirstArray(NonTerminal.VAR_DECL)) {
      Statement statement = VAR_DECL();
      ArrayList<Statement> a = DECLARATION();
      statements.add(statement);
      statements.addAll(a);
    } else if (getFirstArray(NonTerminal.STATEMENT)) {
      // System.out.println(getFirstArray(NonTerminal.STATEMENT));
      Statement statement = STATEMENT();
      ArrayList<Statement> a = DECLARATION();
      statements.add(statement);
      statements.addAll(a);
    }

    return statements;
}

  public static Statement FUN_DECL() {
    match(TokenType.ESC_FUN);
    Token name = getCurrentToken();
    match(TokenType.ESC_IDENTIFIER);
    match(TokenType.ESC_LEFT_PAREN);
    ArrayList<Token> params = PARAMETERS();
    match(TokenType.ESC_RIGHT_PAREN);
    StatementBlock body = BLOCK();
    
    return new FunctionStatement(name, params, body);
  }

  public static Statement VAR_DECL() {
    match(TokenType.ESC_VAR);
    Token name = getCurrentToken();
    match(TokenType.ESC_IDENTIFIER);
    Expression initializer = VAR_INIT();
    match(TokenType.ESC_SEMICOLON);

    return new VariableStatement(name, initializer);
  }

  public static Expression VAR_INIT() {
    Token token = getCurrentToken();
    Expression expression = null;

    if (token != null && token.getType() == TokenType.ESC_EQUAL) {
        match(TokenType.ESC_EQUAL);
        expression = EXPRESSION();
      } else {
        // Epsilon: no hacer nada, la inicialización es opcional
      }
    
      return expression;
}

public static Statement STATEMENT() {
    // System.out.println("Statement");;
    if (getFirstArray(NonTerminal.EXPR_STMT)) {
      ExpressionStatement expressionStatement = new ExpressionStatement(EXPR_STMT());
      return expressionStatement;
    } else if (getFirstArray(NonTerminal.FOR_STMT)) {
      return FOR_STMT();
    } else if (getFirstArray(NonTerminal.IF_STMT)) {
      return IF_STMT();
    } else if (getFirstArray(NonTerminal.PRINT_STMT)) {
      return PRINT_STMT();
    } else if (getFirstArray(NonTerminal.RETURN_STMT)) {
      return RETURN_STMT();
    } else if (getFirstArray(NonTerminal.WHILE_STMT)) {
      return WHILE_STMT();
    } else if (getFirstArray(NonTerminal.BLOCK)) {
      return BLOCK();
    }
    
    return null;
  }

  public static Expression EXPR_STMT() {
    Expression expression = EXPRESSION();
    match(TokenType.ESC_SEMICOLON);

    return expression;
  }

  public static Statement FOR_STMT() {
    match(TokenType.ESC_FOR);
    match(TokenType.ESC_LEFT_PAREN);
    Statement initializar =FOR_STMT_INIT();
    Expression condition = FOR_STMT_COND();
    Statement increment =FOR_STMT_INC();
    match(TokenType.ESC_RIGHT_PAREN);
    Statement body = STATEMENT();

    if (increment == null) 
      body = new StatementBlock(Arrays.asList(body, increment));

    if (condition == null) 
      condition = new LiteralExpression("true");

    LoopStatement loopStatement = new LoopStatement(condition, body);

    if (initializar == null) 
      return loopStatement;

    StatementBlock statementBlock = new StatementBlock(Arrays.asList(initializar, loopStatement));
    return statementBlock;
  }

  public static Statement FOR_STMT_INIT() {
    if (getFirstArray(NonTerminal.VAR_DECL)) {
      return VAR_DECL();
    } else if (getFirstArray(NonTerminal.EXPR_STMT)) {
      ExpressionStatement expressionStatement = new ExpressionStatement(EXPR_STMT());
      return expressionStatement;
    } else {
      match(TokenType.ESC_SEMICOLON);
      return null;
    }
  }

  public static Expression FOR_STMT_COND() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      Expression expression =EXPRESSION();
      match(TokenType.ESC_SEMICOLON);
      return expression;
    } else {
      match(TokenType.ESC_SEMICOLON);
      return null;
    }
  }

  public static Statement FOR_STMT_INC() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      ExpressionStatement expressionStatement = new ExpressionStatement(EXPRESSION());
      return expressionStatement;
    } else {
      // Epsilon
      return null;
    }
  }

  public static Statement IF_STMT() {
    match(TokenType.ESC_IF);
    match(TokenType.ESC_LEFT_PAREN);
    Expression condition = EXPRESSION();
    match(TokenType.ESC_RIGHT_PAREN);
    Statement thenBranch = STATEMENT();
    Statement elseBranch = ELSE_STATMENT();

    IfStatement ifStatement = new IfStatement(condition, thenBranch, elseBranch);
    return ifStatement;
  }

  public static Statement ELSE_STATMENT() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_ELSE) {
      match(TokenType.ESC_ELSE);
      return STATEMENT();
    } else {
      // Epsilon
      return null;
    }
  }

  public static Statement PRINT_STMT() {
    // System.out.println("Entre a print");
    match(TokenType.ESC_PRINT);
    Expression expression = EXPRESSION();
    match(TokenType.ESC_SEMICOLON);
    // System.out.println("Entre a print");

    PrintStatement printStatement = new PrintStatement(expression);
    return printStatement;
  }

  public static Statement RETURN_STMT() {
    match(TokenType.ESC_RETURN);
    Expression value = RETURN_EXP_OPC();
    match(TokenType.ESC_SEMICOLON);

    ReturnStatement returnStatement = new ReturnStatement(value);
    return returnStatement;
  }

  public static Expression RETURN_EXP_OPC() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      Expression expression = EXPRESSION();
      return expression;
    }
    
    return null;
  }

  public static Statement WHILE_STMT() {
    match(TokenType.ESC_WHILE);
    match(TokenType.ESC_LEFT_PAREN);
    Expression condition = EXPRESSION();
    match(TokenType.ESC_RIGHT_PAREN);
    Statement body = STATEMENT();

    LoopStatement loopStatement = new LoopStatement(condition, body);
    return loopStatement;
  }

  public static StatementBlock BLOCK() {
    match(TokenType.ESC_LEFT_BRACE);
    ArrayList<Statement> list = DECLARATION();
    StatementBlock block = new StatementBlock(list);
    match(TokenType.ESC_RIGHT_BRACE);

    return block;
  }

  public static Expression EXPRESSION() {
    Expression expression = ASSIGNMENT();
    return expression;
  }

  public static Expression ASSIGNMENT() {
    Expression leftExpression = CONDITIONAL();
    
    Token name = getPreviousToken();
    Token operator = getCurrentToken();

    Expression rigthExpression = ASSIGNMENT_OPC();

    if (rigthExpression == null)
      return leftExpression;

    AssignmentExpression assignmentExpression = new AssignmentExpression(name, operator, leftExpression);
    return assignmentExpression;
    }

  public static Expression ASSIGNMENT_OPC() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_EQUAL
      || token.getType() == TokenType.ESC_STAR_EQUAL
      || token.getType() == TokenType.ESC_SLASH_EQUAL
      || token.getType() == TokenType.ESC_PLUS_EQUAL
      || token.getType() == TokenType.ESC_MINUS_EQUAL
    ) {
      match(TokenType.ESC_EQUAL);
      Expression expression = EXPRESSION();
      return expression;
    } else {
      return null;
    }
  }
  

  public static Expression CONDITIONAL() {
    Expression leftExpression = LOGIC_OR();
    Expression rigthExpression = CONDITIONAL_P(leftExpression);

    if (rigthExpression == null)
      return leftExpression;

    return rigthExpression;
  }
  
  public static Expression CONDITIONAL_P(Expression leftExpression) {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_QUESTION_MARK) {
      match(TokenType.ESC_QUESTION_MARK);
      Expression thenBranch = EXPRESSION();
      match(TokenType.ESC_COLON);
      Expression elseBranch = CONDITIONAL();

      TernaryExpression ternaryExpression = new TernaryExpression(leftExpression, thenBranch, elseBranch);
      return ternaryExpression;
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression LOGIC_OR() {
    Expression leftExpression = LOGIC_AND();

    Token operator = getCurrentToken();

    Expression rigthExpression = LOGIC_OR_P();

    if (rigthExpression == null)
      return leftExpression;

    LogicalExpression logicalExpression = new LogicalExpression(leftExpression, operator, rigthExpression);
    return logicalExpression;
  }

  public static Expression LOGIC_OR_P() { 
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_OR) {
      match(TokenType.ESC_OR);
      Expression expression = LOGIC_OR();
      return expression;
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression LOGIC_AND() {
    Expression leftExpression = EQUALITY();

    Token operator = getCurrentToken();

    Expression rigthExpression = LOGIC_AND_P();

    if (rigthExpression == null)
      return leftExpression;

    LogicalExpression logicalExpression = new LogicalExpression(leftExpression, operator, rigthExpression);
    return logicalExpression;
  }

  public static Expression LOGIC_AND_P() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_AND) {
      match(TokenType.ESC_AND);
      return LOGIC_AND();
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression EQUALITY()  {
    Expression leftExpression = COMPARISON();

    Token operator = getCurrentToken();

    Expression rigthExpression = EQUALITY_P();

    if (rigthExpression == null)
      return leftExpression;

    RelationalExpression relationalExpression = new RelationalExpression(leftExpression, operator, rigthExpression);
    return relationalExpression;
  }

  public static Expression EQUALITY_P() {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();
      
      if (type == TokenType.ESC_NOT_EQUAL || type == TokenType.ESC_EQUAL_EQUAL) {
        match(type);
        return EQUALITY();
      } else {
        // Epsilon
        return null;
      }
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression COMPARISON() {
    Expression leftExpression = TERM();

    Token operator = getCurrentToken();

    Expression rigthExpression = COMPARISON_P();

    if (rigthExpression == null)
      return leftExpression;

    RelationalExpression relationalExpression = new RelationalExpression(leftExpression, operator, rigthExpression);
    return relationalExpression;
  }

  public static Expression COMPARISON_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_GREATER || type == TokenType.ESC_GREATER_EQUAL ||
          type == TokenType.ESC_LESS || type == TokenType.ESC_LESS_EQUAL) {
        match(type);
        return COMPARISON();
      }  else {
        // Epsilon
        return null;
      } 
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression TERM()  {
    Expression leftExpression = FACTOR();

    Token operator = getCurrentToken();

    Expression rigthExpression = TERM_P();

    if (rigthExpression == null)
      return leftExpression;

    ArithmeticExpression arithmeticExpression = new ArithmeticExpression(leftExpression, operator, rigthExpression);
    return arithmeticExpression;
  }

  public static Expression TERM_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_MINUS || type == TokenType.ESC_PLUS) {
        match(type);
        return TERM();
      }  else if (type == TokenType.ESC_MINUS_MINUS || type == TokenType.ESC_PLUS_PLUS) {
        // Epsilon
        match(type);
        return null;
      } else {
        return null;
      }
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression FACTOR()  {
    Expression leftExpression = UNARY();

    Token operator = getCurrentToken();

    Expression rigthExpression = FACTOR_P();

    if (rigthExpression == null)
      return leftExpression;

    ArithmeticExpression arithmeticExpression = new ArithmeticExpression(leftExpression, operator, rigthExpression);
    return arithmeticExpression;
  }

  public static Expression FACTOR_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_SLASH || type == TokenType.ESC_STAR) {
        match(type);
        return FACTOR();
      }  else {
        // Epsilon
        return null;
      } 
    } else {
      // Epsilon
      return null;
    }
  }

  public static Expression UNARY()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_NOT_EQUAL || type == TokenType.ESC_MINUS) {
        match(type);
        Expression expression = UNARY();
        return expression;
      } else if (getFirstArray(NonTerminal.CALL)) {
        Expression expression = CALL();
        return expression;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public static Expression CALL() {
    Expression leftExpression = PRIMARY();
    Expression rigthExpression = CALL_P(leftExpression);

    if (rigthExpression == null) 
      return leftExpression;

    return rigthExpression;

  }

  public static Expression CALL_P(Expression rightExpression) {
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
            
        case ESC_LEFT_BRACKET:
          match(getCurrentTokenType());
          Expression expression = EXPRESSION();
          match(TokenType.ESC_RIGHT_BRACKET);
          return new ArrayCallExpression(rightExpression, expression);
        default:
          // Epsilon
          return null;
        }
  }

  public static Expression PRIMARY() {
    switch (getCurrentTokenType()) {
      case ESC_TRUE:
      case ESC_FALSE:
      case ESC_NULL:
      case ESC_NUMBER:
      case ESC_FLOATING_NUMBER:
      case ESC_DOUBLE_NUMBER:
      case ESC_STRING:
      case ESC_IDENTIFIER:
        Token t = getCurrentToken();
        match(t.getType());
        return new LiteralExpression(t.getLiteral());

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
  
  public static Expression ARRAY() {
    match(TokenType.ESC_LEFT_BRACKET);
    ArrayList<Expression> arguments = ARGUMENTS(null);
    match(TokenType.ESC_RIGHT_BRACKET);

    LiteralExpression literalExression = new LiteralExpression(arguments);
    return literalExression;
  }

  public static ArrayList<Token> PARAMETERS() {
    Token token = getCurrentToken();

    ArrayList<Token> tokens = new ArrayList<>();

    if (token != null && token.getType() == TokenType.ESC_IDENTIFIER) {
      tokens.add(token);
      match(TokenType.ESC_IDENTIFIER);
      tokens.addAll(PARAMETERS_P());
    }

    return tokens ;
  }

  public static ArrayList<Token> PARAMETERS_P() {
    Token token = getCurrentToken();

    ArrayList<Token> tokens = new ArrayList<>();


    if (token != null && token.getType() == TokenType.ESC_COMMA) {
      match(TokenType.ESC_COMMA);
      tokens.add(getCurrentToken());
      match(TokenType.ESC_IDENTIFIER);
      tokens.addAll(PARAMETERS_P());
    } else {
      // Epsilon
    }

    return tokens;
  }

  public static ArrayList<Expression> ARGUMENTS(ArrayList<Expression> arguments) { 
    if (arguments == null) 
      arguments = new ArrayList<>();

    if (getFirstArray(NonTerminal.EXPRESSION)) {
      arguments.add(EXPRESSION());
      ARGUMENTS_P(arguments);
    }

    return arguments;
  }

  public static void ARGUMENTS_P(ArrayList<Expression> arguments) {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_COMMA) {
      match(TokenType.ESC_COMMA);
      arguments.add(EXPRESSION());
      ARGUMENTS_P(arguments);
    } else {
      // Epsilon
    }
  }
}
