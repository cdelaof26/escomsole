//
// Created by cdelaof26 on 18/02/25.
//

#ifndef ESCOMSOLE_STR_H
#define ESCOMSOLE_STR_H

#include <stdlib.h>

struct str {
    char * text;
    unsigned int length;
};

typedef struct str str;

unsigned int len(const char * d);
unsigned int initCopy(char ** dest, char * source);
int initStrS(str * s, char * d);
int initStrL(str * s, unsigned int length);
int appendChar(str * s, char c);
void unlinkStr(str * s);

#endif //ESCOMSOLE_STR_H
