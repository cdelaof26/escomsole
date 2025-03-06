package interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cristopher
 */
public class Interpreter {
    public static final String ESCOMSOLE_VERSION = "escomsoleJE v0.0.4pre (Mar 06 2025)";
    
    private static int prevState = 0;
    
    /**
     * Given a code line, this function analyses it then executes it
     * 
     * @param code the code to run
     * @param fileLine the specific code of text in the file
     * @return the status code
     */
    public static int execute(String code, int fileLine) {
        if (!code.endsWith("\n")) // The automaton requires to know if there's a \n for some cases
            code += "\n";
        
        // System.out.println("Received data: " + code); // Debug

        int previousTotalTokens = LexicalScanner.tokens.size();
        int status = Status.SCAN_SUCCESS;
        prevState = LexicalScanner.scan(code, fileLine, prevState);

        if (Status.isError(prevState))
            status = prevState;
        
        for (; previousTotalTokens < LexicalScanner.tokens.size(); previousTotalTokens++)
            System.out.println(LexicalScanner.tokens.get(previousTotalTokens));
       
        return status;
    }
    
    /**
     * Given a file path, this function runs all the code within.
     * @param path the path
     * @return the status exit code
     */
    public static int executeFile(String path) {
        File f = new File(path);
        if (!f.exists())
            return Status.FILE_NOT_FOUND; // File not found

        int exitCode = Status.RUN_SUCCESS;
        int line = 1;
        String str1;
        
        // This notation is called try-with-resources, there's no need to 
        // open/close whatever resource we are accessing
        //
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            while (true) {
                str1 = br.readLine();
                if (str1 == null)
                    break;

                exitCode = execute(str1, line);
                line++;
                if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(str1, line, exitCode);
                    break;
                }
            }
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return exitCode;
    }
    
    /**
     * REPL mode, this function waits for user input and then executes it.
     * @return the status exit code
     */
    public static int repl() {
        int exitCode = Status.RUN_SUCCESS;
        int line = 1;
        
        System.out.println(ESCOMSOLE_VERSION + ". Press CTRL + Z to exit.");
        String str1;
        
        try (Scanner s = new Scanner(System.in)) {
            System.out.print(">>> ");
            while (s.hasNext()) {
                str1 = s.nextLine();
                exitCode = execute(str1, line);
                System.out.print(">>> ");
                line++;
                if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(str1, line, exitCode);
                    break;
                }
            };
        }

        return exitCode;
    }

    private static void printError(String line, int fileLine, int errorCode) {
        String data;

        switch (errorCode) {
            case Status.MALFORMED_NUMBER:
                data = "A number is malformed. Near";
            break;
            default: // SYNTAX ERROR
                data = "Invalid syntax. Near";
            break;
        }

        System.err.println(String.format("%s\n    %s\nin line %d\n", data, line, fileLine));
    }
}
