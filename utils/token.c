//
// Created by cdelaof26 on 03/03/25.
//

#include "token.h"
#include <stdio.h>

/**
 * Initializes a token struct given a type and a lexeme
 * @param token the token
 * @param type the type
 * @param line the line in which the token was found
 */
void initToken(Token * token, TOKEN_TYPE type, unsigned int line) {
    token -> type = type;
    token -> line = line;
    token -> lexeme.text = NULL;
    token -> literal.text = NULL;
}

/**
 * Initializes a token struct given a type and a lexeme
 * @param token the token
 * @param type the type
 * @param lexeme the lexeme
 * @param line the line in which the token was found
 * @return 1 if success otherwise 0
 */
int initTokenN(Token * token, TOKEN_TYPE type, char * lexeme, unsigned int line) {
    token -> type = type;
    token -> line = line;
    token -> literal.text = NULL;
    int successStrInit = initStrS(&token -> lexeme, lexeme);
    return !successStrInit;
}

/**
 * Initializes a token struct given a type, a lexeme and a literal
 * @param token the token
 * @param type the type
 * @param lexeme the lexeme
 * @param line the line in which the token was found
 * @param literal the literal
 * @return 1 if success otherwise 0
 */
int initTokenL(Token * token, TOKEN_TYPE type, char * lexeme, char * literal, unsigned int line) {
    token -> type = type;
    token -> line = line;
    int successStrInit = initStrS(&token -> lexeme, lexeme);
    successStrInit += initStrS(&token -> literal, literal);

    return !successStrInit;
}

/**
 * Creates a str representation of a token
 *
 * @param token the token to be represented as str
 * @return the representation
 */
str tokenToString(Token * token) {
    str tokenStr;
    if (!initStrS(&tokenStr, "<"))
        return tokenStr;

    if (!literalTokenInitialized)
        initLiteralTokenType();

    appendCharArray(&tokenStr, LITERAL_TOKEN_TYPE[token -> type]);

    if (token -> lexeme.text != NULL) {
        appendCharArray(&tokenStr, ", Lexeme: ");
        appendCharArray(&tokenStr, token -> lexeme.text);
    }

    if (token -> literal.text != NULL) {
        appendCharArray(&tokenStr, ", Literal: ");
        appendCharArray(&tokenStr, token -> literal.text);
    }

    appendCharArray(&tokenStr, ", Line: ");

    char charLine[10];
    sprintf(charLine, "%d", token -> line);
    appendCharArray(&tokenStr, charLine);

    appendChar(&tokenStr, '>');

    return tokenStr;
}
