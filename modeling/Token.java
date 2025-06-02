package modeling;

import java.util.ArrayList;

public class Token {
  private static final ArrayList<TokenType> lexemeRepresentation = new ArrayList<>();

  static {
    lexemeRepresentation.add(TokenType.ESC_IDENTIFIER);
    lexemeRepresentation.add(TokenType.ESC_NUMBER);
    lexemeRepresentation.add(TokenType.ESC_FLOATING_NUMBER);
    lexemeRepresentation.add(TokenType.ESC_DOUBLE_NUMBER);
    lexemeRepresentation.add(TokenType.ESC_STRING);
  }
  
  private final TokenType type;
  private final String lexeme;
  private final Object literal;
  private final int line;
  private final int column;

  public Token(TokenType type, int line, int column) {
    this.type = type;
    this.lexeme = null;
    this.literal = null;
    this.line = line;
    this.column = column;

  }

  public Token(TokenType type, String lexeme, int line, int column) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = null;
    this.line = line;
    this.column = column;
  }

  public Token(TokenType type, String lexeme, Object literal, int line, int column) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
    this.column = column;
  }

  public TokenType getType() {
    return type;
  }

  public String getStrType() {
    return StrTokenType.STR_TOKEN[type.ordinal()];
  }

  public String getRepresentation() {
    return lexemeRepresentation.contains(type) ? lexeme : getStrType();
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

  public int getColumn() {
    return column;
  }

  public String toString() {
    String data = "<";

    data += type.name().replace("ESC_", "");

    if (lexeme != null) 
      data += ", Lexeme: " + lexeme;

    if (literal != null) 
      data += ", Literal: " + literal;

    if (line != -1) 
      data += ", Line: " + line;

    if (column != -1)
      data += ", Column: " + column;

    data += ">";
    
    return data;
  }
}