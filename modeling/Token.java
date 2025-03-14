package modeling;

/**
 *
 * @author cristopher
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;

    /**
     * Constructs a token given a type and a lexeme
     * 
     * @param type the type
     * @param line the line in which the token was found
     */
    public Token(TokenType type, int line) {
        this.type = type;
        this.lexeme = null;
        this.literal = null;
        this.line = line;
    }
    
    /**
     * Constructs a token given a type and a lexeme
     * @param type the type
     * @param lexeme the lexeme
     * @param line the line in which the token was found
     */
    public Token(TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = null;
        this.line = line;
    }
    
    /**
     * Constructs a token given a type, a lexeme and a literal
     * 
     * @param type the type
     * @param lexeme the lexeme
     * @param literal the literal
     * @param line the line in which the token was found
     */
    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        String data = "<";
        
        data += type.name().replace("ESC_", "");
        
        if (lexeme != null)
            data += ", Lexeme: " + lexeme;
        
        if (literal != null)
            data += ", Literal: " + literal.toString();
        
        if (line != -1)
            data += ", Line: " + line;
        
        data += ">";
        
        return data;
    }
}
