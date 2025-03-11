package modeling;

/**
 *
 * @author cristopher
 */
public enum TokenType {
    // ESC_ stands for ESCOMSOLE, this is needed as some identifiers are defined in C, such as NULL or EOF

    // Tokens de un sólo caracter
    ESC_LEFT_PAREN, ESC_RIGHT_PAREN, ESC_LEFT_BRACE, ESC_RIGHT_BRACE,
    ESC_COMMA, ESC_DOT, ESC_MINUS, ESC_PLUS, ESC_SEMICOLON, ESC_SLASH, ESC_STAR,

    // Tokens de uno o dos caracteres
    ESC_NOT, ESC_NOT_EQUAL,
    ESC_EQUAL, ESC_EQUAL_EQUAL,
    ESC_GREATER, ESC_GREATER_EQUAL,
    ESC_LESS, ESC_LESS_EQUAL,

    // Literales
    ESC_IDENTIFIER, ESC_STRING, ESC_NUMBER,

    // Palabras clave
    ESC_AND, ESC_ELSE, ESC_FALSE, ESC_FUN, ESC_FOR, ESC_IF, ESC_NULL, ESC_OR,
    ESC_PRINT, ESC_RETURN, ESC_TRUE, ESC_VAR, ESC_WHILE,

    ESC_EOF, NONE
}
