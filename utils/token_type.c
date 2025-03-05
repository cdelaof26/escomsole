//
// Created by cdelaof26 on 03/03/25.
//

#include "token_type.h"

char * RESERVED_WORDS[] = {
        "and", "else", "false", "fun", "for", "if", "null", "or",
        "print", "return", "true", "var", "while"
};
int TOTAL_RESERVED_WORDS = 13;

char * LITERAL_TOKEN_TYPE[] = {
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
        "AND", "ELSE", "FALSE", "FUN", "FOR", "IF", "NULL", "OR",
        "PRINT", "RETURN", "TRUE", "VAR", "WHILE",

        "EOF"
};

/**
 * Checks if certain data contains a
 * @param data
 * @return the reserved word ID, if it is not a reserved word -1 will be returned
 */
int isReservedWord(str * data) {
    // TODO: Find a better way to obtain the ID
    for (int i = 0; i < TOTAL_RESERVED_WORDS; i++) {
        if (equalCharArrayCS(RESERVED_WORDS[i], len(RESERVED_WORDS[i]), data -> text, data -> length))
            // 22 is the index for AND in LITERAL_TOKEN_TYPE
            return 22 + i;
    }

    return -1;
}
