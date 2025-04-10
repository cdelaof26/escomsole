package parser;

import java.util.List;
import modeling.Token;
import modeling.TokenType;

public class ParserUtils {
    private List<Token> tokens;
    private int current;

    public ParserUtils(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    public Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    public void consume(TokenType type, String message) {
        if (check(type)) {
            advance();
        } else {
            throw error(peek(), message);
        }
    }

    public boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    public boolean isAtEnd() {
        return peek().getType() == TokenType.ESC_EOF;
    }

    public Token peek() {
        return tokens.get(current);
    }

    public Token previous() {
        return tokens.get(current - 1);
    }

    public RuntimeException error(Token token, String message) {
        return new RuntimeException("[Línea " + token.getLine() + "] Error en '" + token.getLexeme() + "': " + message);
    }

    public TokenType getCurrentTokenType() {
        return peek().getType();
    }

    public int getCurrentPosition() {
        return current;
    }

    public void setCurrentPosition(int pos) {
        this.current = pos;
    }
}