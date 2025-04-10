package parser;

import modeling.TokenType;
import interpreter.LexicalScanner;
import modeling.NoTerminales;

public class Parser {
    private ParserUtils utils;

    public Parser() {
        this.utils = new ParserUtils(LexicalScanner.tokens);
    }

    public boolean parse() {
      PROGRAM();
        System.out.println("Programa válido");
        return true;
    }

    private void PROGRAM() {
        DECLARATION();
    }

    private void DECLARATION() {
      Object[] production = First.first(NoTerminales.DECLARATION);
        
      System.out.println(production);

        if (production == null) error("DECLARATION");

        for (Object symbol : production) {
            if (symbol instanceof NoTerminales) call((NoTerminales) symbol);
            else utils.consume((TokenType) symbol, "Token inesperado en DECLARATION");
        }
    }

    private void VAR_DECL() {
        utils.consume(TokenType.ESC_VAR, "Se esperaba 'var'");
        utils.consume(TokenType.ESC_IDENTIFIER, "Se esperaba un identificador");
        VAR_INIT();
        utils.consume(TokenType.ESC_SEMICOLON, "Se esperaba ';'");
    }

    private void VAR_INIT() {
        if (utils.match(TokenType.ESC_EQUAL)) {
            EXPRESSION();
        }
    }

    private void STATEMENT() {
        Object[] production = First.first(NoTerminales.STATEMENT);

        if (production == null) error("STATEMENT");

        for (Object symbol : production) {
            if (symbol instanceof NoTerminales) call((NoTerminales) symbol);
            else utils.consume((TokenType) symbol, "Token inesperado en STATEMENT");
        }
    }

    private void EXPR_STMT() {
        EXPRESSION();
        utils.consume(TokenType.ESC_SEMICOLON, "Se esperaba ';'");
    }

    private void IF_STMT() {
        utils.consume(TokenType.ESC_IF, "Se esperaba 'if'");
        utils.consume(TokenType.ESC_LEFT_PAREN, "Se esperaba '(' después de 'if'");
        EXPRESSION();
        utils.consume(TokenType.ESC_RIGHT_PAREN, "Se esperaba ')'");
        STATEMENT();
        ELSE_STATEMENT();
    }

    private void ELSE_STATEMENT() {
        if (utils.match(TokenType.ESC_ELSE)) {
            STATEMENT();
        }
    }

    private void WHILE_STMT() {
        utils.consume(TokenType.ESC_WHILE, "Se esperaba 'while'");
        utils.consume(TokenType.ESC_LEFT_PAREN, "Se esperaba '(' después de 'while'");
        EXPRESSION();
        utils.consume(TokenType.ESC_RIGHT_PAREN, "Se esperaba ')'");
        STATEMENT();
    }

    private void BLOCK() {
        utils.consume(TokenType.ESC_LEFT_BRACE, "Se esperaba '{'");
        while (!utils.check(TokenType.ESC_RIGHT_BRACE) && !utils.isAtEnd()) {
            DECLARATION();
        }
        utils.consume(TokenType.ESC_RIGHT_BRACE, "Se esperaba '}'");
    }

    private void PRINT_STMT() {
        utils.consume(TokenType.ESC_PRINT, "Se esperaba 'print'");
        EXPRESSION();
        utils.consume(TokenType.ESC_SEMICOLON, "Se esperaba ';'");
    }

    private void EXPRESSION() {
        // Implementación mínima
        if (!utils.match(TokenType.ESC_IDENTIFIER, TokenType.ESC_NUMBER, TokenType.ESC_STRING)) {
            throw utils.error(utils.peek(), "Se esperaba una expresión válida");
        }
    }

    // Llama funciones por nombre desde la producción actual
    private void call(NoTerminales symbol) {
        switch (symbol) {
          case VAR_DECL:
            VAR_DECL();
            break;
          case STATEMENT:
            STATEMENT();
            break;
          case EXPR_STMT:
            EXPR_STMT();
            break;
          case PRINT_STMT:
            PRINT_STMT();
            break;
          case IF_STMT:
            IF_STMT();
            break;
          case ELSE_STATEMENT:
            ELSE_STATEMENT();
            break;
          case WHILE_STMT:
            WHILE_STMT();
            break;
          case BLOCK:
            BLOCK();
            break;
          case VAR_INIT:
            VAR_INIT();
            break;
          case EXPRESSION:
            EXPRESSION();
            break;
          default:
            throw new RuntimeException("Producción no implementada: " + symbol);
        }
    }

    private void error(String mensaje) {
        throw new RuntimeException("Error de sintaxis en " + mensaje + ", token actual: " + utils.peek());
    }
}