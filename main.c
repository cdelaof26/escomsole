#include <stdio.h>
#include "utils/str.h"

void execute(str * line) {
    // This functions takes a str (struct) as parameter
    // To create a str you can use:
    //    str myString;
    //    initStrS(&myString, "Hello World!");
    printf("Impresion de la cadena: %s", line -> text);
}

int executeFile(const char * path) {
    FILE * f = fopen(path, "rt");
    if (f == NULL)
        return 1; // File not found

    int exitCode = 0;

    // Execute file logic here

    return exitCode;
}

int repl() {
    int exitCode = 0;

    str string;
    initStrL(&string, 1000);

    printf("Ingresa diferentes cadenas hasta que decidas terminar la ejecucion. (presiona CTRL + Z para salir): \n");

    while (1) {
        printf(">>> ");
        char * result = fgets(string.text, 1000, stdin);

        if(result == NULL)
            break;

        execute(&string);
    }

    printf("Fin del programa... \n");
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
