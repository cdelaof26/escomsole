//
// Created by cdelaof26 on 03/03/25.
//

#ifndef ESCOMSOLE_TOKEN_H
#define ESCOMSOLE_TOKEN_H

#include <stdio.h>
#include "token_type.h"
#include "str.h"

struct Token {
    TOKEN_TYPE type;
    str lexeme;
    str literal;
    unsigned int line;
};

typedef struct Token Token;

int initEOFToken(Token * token);
void initToken(Token * token, TOKEN_TYPE type, unsigned int line);
int initTokenN(Token * token, TOKEN_TYPE type, char * lexeme, unsigned int line);
int initTokenL(Token * token, TOKEN_TYPE type, char * lexeme, char * literal, unsigned int line);

str tokenToString(Token * token);

#endif //ESCOMSOLE_TOKEN_H
