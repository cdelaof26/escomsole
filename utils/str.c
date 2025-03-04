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
 * Copies source to dest. The shorter array determines how much data will be copied.
 *
 * @param dest the destination array
 * @param destLength the length for the destination array
 * @param source the source array
 * @param sourceLength the length for the source array
 */
void copy(char * dest, unsigned int destLength, const char * source, unsigned int sourceLength) {
    int i = 0;
    for (; i < destLength && i < sourceLength; i++)
        dest[i] = source[i];

    dest[destLength - 1] = '\0';
}

/**
 * Compares the contents in two char arrays.
 * Note: This is case sensitive
 *
 * @param s1 the first array
 * @param s1Length the length for s1
 * @param s2 the second array
 * @param s2Length the length for s2
 * @return 1 if equal otherwise 0
 */
int equalCharArrayCS(const char * s1, unsigned int s1Length, const char * s2, unsigned int s2Length) {
    if (s1Length != s2Length)
        return 0;

    for (int i = 0; i < s1Length; i++)
        if (s1[i] != s2[i])
            return 0;

    return 1;
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

/**
 * Initializes an string making it usable given an initial length.
 * Do NOT use with initialized str, use unlinkStr(str *) instead.
 *
 * @param s the pointer
 * @param length the new initial len
 * @return 1 if succeed otherwise 0
 */
int initStrL(str * s, const unsigned int length) {
    s -> length = length;
    s -> text = (char *) malloc(sizeof(char) * length);
    int succeed = s -> text != NULL;
    if (succeed && length > 0)
        s -> text[0] = '\0';

    return succeed;
}

/**
 * Appends a character to the str
 *
 * @param s the string
 * @param c the character to append
 * @return 1 if succeed otherwise 0
 */
int appendChar(str * s, char c) {
    // This handles a case in which a string might have space left (last index is not \0)
    unsigned int stringEnd = 0;
    if (s -> length > 0)
        stringEnd = len(s -> text);

    const unsigned int newLength = (stringEnd > s -> length ? s -> length : stringEnd) + 1;
    char * tmp = (char *) realloc(s -> text, sizeof(char) * newLength);
    if (tmp == NULL)
        return 0;

    tmp[newLength - 1] = c;
    s -> length = newLength;
    s -> text = tmp;

    return 1;
}

/**
 * Appends a char array to a str
 *
 * @param s the string
 * @param array the char array to append
 * @return 1 if succeed otherwise 0
 */
int appendCharArray(str * s, char * array) {
    const unsigned int arrayLength = len(array);
    const unsigned int newLength = s -> length + arrayLength;
    char * tmp = (char *) realloc(s -> text, sizeof(char) * newLength);
    if (tmp == NULL)
        return 0;

    copy(tmp + s -> length, newLength, array, arrayLength);
    s -> length = newLength;
    s -> text = tmp;

    return 1;
}
