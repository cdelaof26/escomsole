//
// Created by cdelaof26 on 04/03/25.
//

#include "lexical.h"

unsigned int totalTokens = 0;

/**
 * Array holding all generated/found tokens
 */
Token ** tokens = NULL;

/**
 * Appends a token into the tokens array
 * @param t the token to add
 * @return 1 if success otherwise 0
 */
int appendToken(Token * t) {
    unsigned int tmpTotal = totalTokens + 1;
    Token ** tmp;
    if (tokens == NULL)
        tmp = (Token **) malloc(tmpTotal * sizeof(Token *));
    else
        tmp = (Token **) realloc(tokens, tmpTotal * sizeof(Token *));

    if (tmp == NULL)
        return 0;

    tmp[totalTokens] = t;
    totalTokens = tmpTotal;
    tokens = tmp;

    return 1;
}

/**
 * This function implements a lexical scanner
 * @param code the line to analyse
 * @param fileLine the specific line of text in the file
 * @return the automaton status code
 */
int scan(str * code, int fileLine) {
    char c;
    int state = 0;
    str * lexeme = (str *) malloc(sizeof(str *));
    if (lexeme == NULL || !initStrL(lexeme, 1)) return -1;

    // printf("line %s\n", code -> text); // Debug
    // printf("LINE #%d ; len %d\n", fileLine, code -> length); // Debug

    for (int i = 0; i < code -> length; i++) {
        c = code -> text[i];

        /**
         * Required automata:
         *  - Comment analyser
         *  - Identifiers and reserved words analyser : DONE
         *  - Operators analyser
         *  - Punctuation analyser
         *  - Number analyser: this may require an evaluation function
         *  - String analyser
         *  - Handle error
         *  - Extra automata
         *      - ++, --
         *      - Ternary operation
         *      - Array bracket notation
         */

        // printf("%c : state %d\n", c, state); // Debug

        if (state == 0) {
            if (isalpha(c)) { // IDENTIFIERS and reserved words
                state = 13;
                if (!appendChar(lexeme, c)) return -1;
            }

            continue;
        }

        if (state == 13) {
            if (isalnum(c)) { // IDENTIFIERS and reserved words
                if (!appendChar(lexeme, c)) return -1;
            } else { // end of identifier or reserved word
                Token * t = (Token *) malloc(sizeof(Token));
                if (t == NULL) return -1;

                int type = isReservedWord(lexeme);
                if (type == -1) {
                    if (!initTokenN(t, ESC_IDENTIFIER, lexeme -> text, fileLine)) return -1;
                } else {
                    initToken(t, type, fileLine);
                }

                unlinkStr(lexeme);
                if (!initStrL(lexeme, 1)) return -1;

                if (!appendToken(t)) return -1;
                state = 0;
                i--;
            }

            continue;
        }
    }

    return state;
}

/**
 * Given a code line, this function analyses it then executes it
 * @param code the code to run
 * @param fileLine the specific code of text in the file
 * @return the status code
 */
int execute(str * code, int fileLine) {
    // printf("Received data: %s", code -> text); // Debug

    unsigned int previousTotalTokens = totalTokens;
    int status = SCAN_SUCCESS;
    int scanStatus = scan(code, fileLine);

    if (scanStatus == -1)
        status = MEM_ERROR; // Memory error, append functions in str.h might trigger this

    // printf("prev %d ; total %d\n", previousTotalTokens, totalTokens); // Debug
    if (totalTokens > 0)
        for (; previousTotalTokens < totalTokens; previousTotalTokens++)
            printf("%s\n", tokenToString(tokens[previousTotalTokens]).text);

    return status;
}

/**
 * Given a file path, this function runs all the code within.
 * @param path the path
 * @return the status exit code
 */
int executeFile(const char * path) {
    FILE * f = fopen(path, "rt");
    if (f == NULL)
        return 1; // File not found

    int exitCode = RUN_SUCCESS;

    // char r;
    int r; // fgetc returns an integer

    // Instead of using a char array, use a str struct
    // char words[1000];
    str str1;
    initStrL(&str1, 1);
    int line = 1;

    while((r = fgetc(f)) != EOF) {
        appendChar(&str1, (char) r);

        if (r != '\n')
            continue;

        execute(&str1, line);
        unlinkStr(&str1);
        initStrL(&str1, 1);
        line++;
    }

    // printf("%s", words);
    // We need to check if the last line was executed. Unlink sets the length back to zero,
    // but we're reinitializing the string back to 1
    line++;
    if (str1.length != 1)
        execute(&str1, line);
    unlinkStr(&str1);

    return exitCode;
}

/**
 * REPL mode, this function waits for user input and then executes it.
 * @return the status exit code
 */
int repl() {
    int exitCode = RUN_SUCCESS;

    int line = 1;
    str string;
    if (!initStrL(&string, REPL_BUFFER_SIZE)) return MEM_ERROR;

    printf("%s. Press CTRL + Z to exit.\n", ESCOMSOLE_VERSION);

    while (1) {
        printf(">>> ");
        char * result = fgets(string.text, REPL_BUFFER_SIZE, stdin);

        if(result == NULL)
            break;

        execute(&string, line);
        line++;
    }

    return exitCode;
}
