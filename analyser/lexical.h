//
// Created by cdelaof26 on 04/03/25.
//

#ifndef ESCOMSOLE_LEXICAL_H
#define ESCOMSOLE_LEXICAL_H

#include <stdio.h>
#include <ctype.h>
#include "../utils/str.h"
#include "../utils/token.h"

#define ESCOMSOLE_VERSION "escomsole v0.0.3pre (Mar 04 2025)"
#define REPL_BUFFER_SIZE 1024

#define RUN_SUCCESS  0
#define SCAN_SUCCESS 0
#define MEM_ERROR    1
#define SCAN_ERROR   2

extern unsigned int totalTokens;
extern Token ** tokens;
int appendToken(Token * t);

int scan(str * code, int fileLine);
int execute(str * code, int fileLine);

int executeFile(const char * path);
int repl();

#endif //ESCOMSOLE_LEXICAL_H
