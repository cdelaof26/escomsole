#include <stdio.h>
#include "utils/str.h"

#define ESCOMSOLE_VERSION "escomsole v0.0.2 (Feb 19 2025)"
#define REPL_BUFFER_SIZE 1024

void execute(str * line) {
    // This functions takes a str (struct) as parameter
    // To create a str you can use:
    //    str myString;
    //    initStrS(&myString, "Hello World!");
    printf("Received data: %s", line -> text);
}

int executeFile(const char * path) {
    FILE * f = fopen(path, "rt");
    if (f == NULL)
        return 1; // File not found

    int exitCode = 0;

    // char r;
    int r; // fgetc returns an integer

    // Instead of using a char array, use a str struct
    // char words[1000];
    str str1;
    initStrL(&str1, 1);

    while((r = fgetc(f)) != EOF) {
        appendChar(&str1, (char) r);

        if (r != '\n')
            continue;

        execute(&str1);
        unlinkStr(&str1);
        initStrL(&str1, 1);
    }

    // printf("%s", words);
    // We need to check if the last line was executed. Unlink sets the length back to zero,
    // but we're reinitializing the string back to 1
    if (str1.length != 1)
        execute(&str1);
    unlinkStr(&str1);

    return exitCode;
}

int repl() {
    int exitCode = 0;

    str string;
    initStrL(&string, REPL_BUFFER_SIZE);

    printf("%s. Press CTRL + Z to exit.\n", ESCOMSOLE_VERSION);

    while (1) {
        printf(">>> ");
        char * result = fgets(string.text, REPL_BUFFER_SIZE, stdin);

        if(result == NULL)
            break;

        execute(&string);
    }

    return exitCode;
}

int main(int argc, char const * argv[]) {
    if (argc == 1) // No user provided arguments
        return repl();

    if (argc == 2) { // One user provided argument
        int status = executeFile(argv[1]);
        if (status == 1)
            printf("File '%s' not found\n", argv[1]);

        return status;
    }

    // Two or more user provided arguments
    fprintf(stderr, "Usage:\n  %s : REPL mode\n  %s path/to/file : execute file\n", argv[0], argv[0]);
    return 1;
}
