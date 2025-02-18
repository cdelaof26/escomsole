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
void unlinkStr(str * s);

#endif //ESCOMSOLE_STR_H
