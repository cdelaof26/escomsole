#include <stdio.h>
#include "analyser/lexical.h"


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
