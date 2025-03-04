//
// Created by cdelaof26 on 03/03/25.
//

#include "token_type.h"

int literalTokenInitialized = 0;

/**
 * This function initializes the array LITERAL_TOKEN_TYPE which maps a number to a char *
 */
void initLiteralTokenType() {
    // For no apparent reason, it won't let me use LITERAL_TOKEN_TYPE,
    // so this ended requiring a temporal variable
    //
    char * tmp[] = {
            "LEFT_PAREN", "RIGHT_PAREN", "LEFT_BRACE", "RIGHT_BRACE",
            "COMMA", "DOT", "MINUS", "PLUS", "SEMICOLON", "SLASH", "STAR",

            // Tokens de uno o dos caracteres
            "BANG", "BANG_EQUAL",
            "EQUAL", "EQUAL_EQUAL",
            "GREATER", "GREATER_EQUAL",
            "LESS", "LESS_EQUAL",

            // Literales
            "IDENTIFIER", "STRING", "NUMBER",

            // Palabras clave
            "AND", "ELSE", "FALSE", "FUN", "FOR", "IF", "NULL_", "OR",
            "PRINT", "RETURN", "TRUE", "VAR", "WHILE",

            "EOF"
    };

    literalTokenInitialized = 1;
    LITERAL_TOKEN_TYPE = tmp;
}
