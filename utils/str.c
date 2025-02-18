//
// Created by cdelaof26 on 18/02/25.
//

#include "str.h"

/**
 * Returns the len for a given char array. This is NOT memory safe!
 * This SHOULD be used only with char arrays that FOR SURE contain the end
 * termination (\0).
 *
 * @param d the char array
 * @return the len for the char array
 */
unsigned int len(const char * d) {
    unsigned int i = 0;
    while (d[i] != '\0')
        i++;

    return i;
}

/**
 * This copies all contents in source to dest.
 * Note that dest will be initialized in source's len, loosing all the data in the process.
 *
 * @param dest the destination for the copy
 * @param source the source data
 * @return the dest length if succeed otherwise 0
 */
unsigned int initCopy(char ** dest, char * source) {
    unsigned int sourceLength = len(source);
    char * tmp = (char *) malloc(sizeof(char) * sourceLength);
    if (tmp == NULL)
        return 0;

    for (int i = 0; i < sourceLength; i++)
        tmp[i] = source[i];

    *dest = tmp;
    return sourceLength;
}

/**
 * Initializes an string making it usable given a initial char array,
 * the char array will be copied.
 * Do NOT use with initialized str, use unlinkStr(str *) instead.
 * Note: the char array NEEDS TO HAVE the end terminator null (\0).
 *
 * @param s the pointer
 * @param d the initial value
 * @return 1 if succeed otherwise 0
 */
int initStrS(str * s, char * d) {
    s -> length = initCopy(&(s -> text), d);
    return s -> length != 0;
}

/**
 * Frees the internal char array and sets the len to 0.
 * Use initStrL or initStrS to reuse s.
 * Note: do NOT use with non-initialized strings as freeing a empty pointer might cause problems
 *
 * @param s the string to remove from memory
 */
void unlinkStr(str * s) {
    s -> length = 0;
    if (s -> text != NULL)
        free(s -> text);
}

int initStrL(str * s, const unsigned int length) {
    s -> length = length;
    s -> text = (char *) malloc(sizeof(char) * length);
    int succeed = s->text != NULL;
    if(succeed && length > 0)
        s -> text[0] = '\0';

    return succeed;}

