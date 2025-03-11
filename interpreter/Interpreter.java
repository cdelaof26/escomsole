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
    public static final String ESCOMSOLE_VERSION = "escomsoleJE v0.0.3-2pre (Mar 10 2025)";
    
    private static int prevState = 0;
    
    /**
     * Given a code line, this function analyses it then executes it
     * 
     * @param codeSnippet the code to run
     * @param lineNumber the line number in the file
     * @return the status code
     */
    public static int execute(String codeSnippet, int lineNumber) {
        if (!codeSnippet.endsWith("\n")) // The automaton requires to know if there's a \n for some cases
            codeSnippet += "\n";
        
        // System.out.println("Received data: " + code); // Debug

        int previousTotalTokens = LexicalScanner.tokens.size();
        int status = Status.SCAN_SUCCESS;
        prevState = LexicalScanner.scan(codeSnippet, lineNumber, prevState);

        if (Status.isError(prevState))
            status = prevState;
        
        for (; previousTotalTokens < LexicalScanner.tokens.size(); previousTotalTokens++)
            System.out.println(LexicalScanner.tokens.get(previousTotalTokens));
       
        return status;
    }
    
    /**
     * Given a file path, this function runs all the code within.
     * @param filePath the path
     * @return the status exit code
     */
    public static int executeFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists())
            return Status.FILE_NOT_FOUND; // File not found

        int exitCode = Status.RUN_SUCCESS;
        int lineNumber = 1;
        String fileLine;
        
        // This notation is called try-with-resources, there's no need to 
        // open/close whatever resource we are accessing
        //
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            while (true) {
                fileLine = br.readLine();
                if (fileLine == null)
                    break;

                exitCode = execute(fileLine, lineNumber);
                if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(fileLine, lineNumber, exitCode);
                    break;
                }
                
                lineNumber++;
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
        int lineNumber = 1;
        
        System.out.println(ESCOMSOLE_VERSION + ". Press CTRL + Z to exit.");
        String fileLine;
        
        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                System.out.print(">>> ");
                fileLine = s.nextLine();
                if (fileLine == null || fileLine.isEmpty())
                    break;
                
                exitCode = execute(fileLine, lineNumber);
                if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(fileLine, lineNumber, exitCode);
                }
                
                lineNumber++;
            }
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
