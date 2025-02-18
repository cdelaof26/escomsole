#include <stdio.h>
#include "utils/str.h"

void execute(str * line) {
    // This functions takes a str (struct) as parameter
    // To create a str you can use:
    //    str myString;
    //    initStrS(&myString, "Hello World!");
    printf("%s\n", line -> text);
}

int executeFile(const char * path) {
    FILE * f = fopen(path, "rt");
    if (f == NULL)
        return 1; // File not found

    fclose(f);

    int exitCode = 0;

    // Execute file logic here

    return exitCode;
}

int repl() {
    int exitCode = 0;

    while (0 /* 1 */) {
        // REPL logic here
    }

    return exitCode;
}

int main(int argc, char const * argv[]) {
    if (argc == 1)
        return repl();

    if (argc == 2) {
        int status = executeFile(argv[1]);
        if (status == 1)
            printf("File '%s' not found\n", argv[1]);

        return status;
    }

    printf("Usage:\n  %s : REPL mode\n  %s path/to/file : execute file\n", argv[0], argv[0]);
    return 1;
}
