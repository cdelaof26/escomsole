package parser;

import interpreter.LexicalScanner;
import java.util.ArrayList;

import modeling.NonTerminal;
import modeling.Token;
import modeling.TokenType;

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


  public static void PROGRAM() {
    // System.out.println("Program");;
    DECLARATION();
  }

  public static void DECLARATION() {
    Token token = getCurrentToken();

    // .out.println("Declaration" + token.getType());
    // System.out.println(SyntacticGrammar.first(NonTerminal.STATEMENT));
    // System.out.println(getFirstArray(NonTerminal.STATEMENT));

    if (token == null || token.getType() == TokenType.ESC_EOF) {
      // Epsilon: fin de las declaraciones, no hacer nada
      return;
    }
    
    if (getFirstArray(NonTerminal.FUN_DECL)) {
      FUN_DECL();
      DECLARATION();
    } else if (getFirstArray(NonTerminal.VAR_DECL)) {
      VAR_DECL();
      DECLARATION();
    } else if (getFirstArray(NonTerminal.STATEMENT)) {
      // System.out.println(getFirstArray(NonTerminal.STATEMENT));
      STATEMENT();
      DECLARATION();
    }
}

  public static void FUN_DECL() {
    match(TokenType.ESC_FUN);
    match(TokenType.ESC_IDENTIFIER);
    match(TokenType.ESC_LEFT_PAREN);
    PARAMETERS();
    match(TokenType.ESC_RIGHT_PAREN);
    BLOCK();
  }

  public static void VAR_DECL() {
    match(TokenType.ESC_VAR);
    match(TokenType.ESC_IDENTIFIER);
    VAR_INIT();
    match(TokenType.ESC_SEMICOLON);
  }

  public static void VAR_INIT() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_EQUAL) {
        match(TokenType.ESC_EQUAL);
        EXPRESSION();
    } else {
        // Epsilon: no hacer nada, la inicialización es opcional
    }
}

public static void STATEMENT() {
    // System.out.println("Statement");;
    if (getFirstArray(NonTerminal.EXPR_STMT)) {
      EXPR_STMT();
    } else if (getFirstArray(NonTerminal.FOR_STMT)) {
      FOR_STMT();
    } else if (getFirstArray(NonTerminal.IF_STMT)) {
      IF_STMT();
    } else if (getFirstArray(NonTerminal.PRINT_STMT)) {
      PRINT_STMT();
    } else if (getFirstArray(NonTerminal.RETURN_STMT)) {
      RETURN_STMT();
    } else if (getFirstArray(NonTerminal.WHILE_STMT)) {
      WHILE_STMT();
    } else if (getFirstArray(NonTerminal.BLOCK)) {
      BLOCK();
    }
  }

  public static void EXPR_STMT() {
    EXPRESSION();
    match(TokenType.ESC_SEMICOLON);
  }

  public static void FOR_STMT() {
    match(TokenType.ESC_FOR);
    match(TokenType.ESC_LEFT_PAREN);
    FOR_STMT_INIT();
    FOR_STMT_COND();
    FOR_STMT_INC();
    match(TokenType.ESC_RIGHT_PAREN);
    STATEMENT();
  }

  public static void FOR_STMT_INIT() {
    if (getFirstArray(NonTerminal.VAR_DECL)) {
      VAR_DECL();
    } else if (getFirstArray(NonTerminal.EXPR_STMT)) {
      EXPR_STMT();
    } else {
      match(TokenType.ESC_SEMICOLON);
    }
  }

  public static void FOR_STMT_COND() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      EXPRESSION();
      match(TokenType.ESC_SEMICOLON);
    } else {
      match(TokenType.ESC_SEMICOLON);
    }
  }

  public static void FOR_STMT_INC() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      EXPRESSION();
    } else {
      // Epsilon
    }
  }

  public static void IF_STMT() {
    match(TokenType.ESC_IF);
    match(TokenType.ESC_LEFT_PAREN);
    EXPRESSION();
    match(TokenType.ESC_RIGHT_PAREN);
    STATEMENT();
    ELSE_STATMENT();
  }

  public static void ELSE_STATMENT() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_ELSE) {
      match(TokenType.ESC_ELSE);
      STATEMENT();
    } else {
      // Epsilon
    }
  }

  public static void PRINT_STMT() {
    // System.out.println("Entre a print");
    match(TokenType.ESC_PRINT);
    EXPRESSION();
    match(TokenType.ESC_SEMICOLON);
    // System.out.println("Entre a print");
  }

  public static void RETURN_STMT() {
    match(TokenType.ESC_RETURN);
    RETURN_EXP_OPC();
    match(TokenType.ESC_SEMICOLON);
  }

  public static void RETURN_EXP_OPC() {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      EXPRESSION();
    } else {
      // Epsilon
    }
  }

  public static void WHILE_STMT() {
    match(TokenType.ESC_WHILE);
    match(TokenType.ESC_LEFT_PAREN);
    EXPRESSION();
    match(TokenType.ESC_RIGHT_PAREN);
    STATEMENT();
  }

  public static void BLOCK() {
    match(TokenType.ESC_LEFT_BRACE);
    DECLARATION();
    match(TokenType.ESC_RIGHT_BRACE);
  }

  public static void EXPRESSION() {
    ASSIGNMENT();
  }

  public static void ASSIGNMENT() {
      LOGIC_OR();
      ASSIGNMENT_OPC();
  }

  public static void ASSIGNMENT_OPC() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_EQUAL) {
      match(TokenType.ESC_EQUAL);
      EXPRESSION();
    } else {
      // Epsilon
    }
  }

  public static void LOGIC_OR() {
      LOGIC_AND();
      LOGIC_OR_P();
  }

  public static void LOGIC_OR_P() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_OR) {
      match(TokenType.ESC_OR);
      LOGIC_OR();
    } else {
      // Epsilon
    }
  }

  public static void LOGIC_AND()  {
      EQUALITY();
      LOGIC_AND_P();
  }

  public static void LOGIC_AND_P() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_AND) {
      match(TokenType.ESC_AND);
      LOGIC_AND();
    } else {
      // Epsilon
    }
  }

  public static void EQUALITY()  {
      COMPARISON();
      EQUALITY_P();
  }

  public static void EQUALITY_P() {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();
      
      if (type == TokenType.ESC_NOT_EQUAL || type == TokenType.ESC_EQUAL_EQUAL) {
        match(type);
        EQUALITY();
      } /* else {
        // Epsilon
      } */
    } else {
      // Epsilon
    }
  }

  public static void COMPARISON()  {
      TERM();
      COMPARISON_P();
  }

  public static void COMPARISON_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_GREATER || type == TokenType.ESC_GREATER_EQUAL ||
          type == TokenType.ESC_LESS || type == TokenType.ESC_LESS_EQUAL) {
        match(type);
        COMPARISON();
      } /* else {
        // Epsilon
      } */
    } else {
      // Epsilon
    }
  }

  public static void TERM()  {
      FACTOR();
      TERM_P();
  }

  public static void TERM_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_MINUS || type == TokenType.ESC_PLUS ||
          type == TokenType.ESC_MINUS_MINUS || type == TokenType.ESC_PLUS_PLUS) {
        match(type);
        TERM();
      } /* else {
        // Epsilon
      } */
    } else {
      // Epsilon
    }
  }

  public static void FACTOR()  {
      UNARY();
      FACTOR_P();
  }

  public static void FACTOR_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_SLASH || type == TokenType.ESC_STAR) {
        match(type);
        FACTOR();
      } /* else {
        // Epsilon
      } */
    } else {
      // Epsilon
    }
  }

  public static void UNARY()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_NOT_EQUAL || type == TokenType.ESC_MINUS) {
        match(type);
        UNARY();
      } else if (getFirstArray(NonTerminal.CALL)) {
        CALL();
      }
    } 
  }

  public static void CALL()  {
      PRIMARY();
      CALL_P();
  }

  public static void CALL_P()  {
    if (getCurrentToken() != null) {
      TokenType type = getCurrentToken().getType();

      if (type == TokenType.ESC_LEFT_PAREN) {
        match(TokenType.ESC_LEFT_PAREN);
        ARGUMENTS();
        match(TokenType.ESC_RIGHT_PAREN);
      } /* else {
        // Epsilon
      } */
    } else {
      // Epsilon
    }
  }

  public static void PRIMARY()  {
    Token token = getCurrentToken();

    if (token != null) {
      switch (token.getType()) {
        case ESC_TRUE:
          match(TokenType.ESC_TRUE);
          break;
        case ESC_FALSE:
            match(TokenType.ESC_FALSE);
            break;
        case ESC_NULL:
            match(TokenType.ESC_NULL);
            break;
        case ESC_NUMBER:
            match(TokenType.ESC_NUMBER);
            break;
        case ESC_STRING:
            match(TokenType.ESC_STRING);
            break;
        case ESC_IDENTIFIER:
          match(TokenType.ESC_IDENTIFIER);
          break;
        case ESC_LEFT_PAREN:
          match(TokenType.ESC_LEFT_PAREN);
          EXPRESSION();
          match(TokenType.ESC_RIGHT_PAREN);
          break;
        default:
      }
    } 
  }

  public static void PARAMETERS() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_IDENTIFIER) {
      match(TokenType.ESC_IDENTIFIER);
      PARAMETERS_P();
    } else {
      // Epsilon
    }
  }

  public static void PARAMETERS_P() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_COMMA) {
      match(TokenType.ESC_COMMA);
      match(TokenType.ESC_IDENTIFIER);
      PARAMETERS_P();
    } else {
      // Epsilon
    }
  }

  public static void ARGUMENTS()  {
    if (getFirstArray(NonTerminal.EXPRESSION)) {
      EXPRESSION();
      ARGUMENTS_P();
    } else {
      // Epsilon
    }
  }

  public static void ARGUMENTS_P() {
    Token token = getCurrentToken();

    if (token != null && token.getType() == TokenType.ESC_COMMA) {
      match(TokenType.ESC_COMMA);
      EXPRESSION();
      ARGUMENTS_P();
    } else {
      // Epsilon
    }
  }
}
