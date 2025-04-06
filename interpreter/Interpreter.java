package interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import modeling.Token;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class Interpreter {
    public static final String ESCOMSOLE_VERSION = "escomsoleJE v0.0.4 (Apr 06 2025)";
    
    public static final int FILE_NOT_FOUND = 1;
    public static final int INVALID_ESCOMSOLE_CALL = -1;
    
    private static int prevState = 0;
    
    private static final ArrayList<String> code = new ArrayList<>();
    private static boolean showPrevNext = true;
    
    /**
     * @param codeSnippet the snippet
     * @param lineNumber the line in the file
     * @return an status code
     */
    private static int generateTokens(String codeSnippet, int lineNumber) {
        if (codeSnippet == null) {
            LexicalScanner.tokens.add(new Token(TokenType.ESC_EOF, "$", -1));
            // System.out.println(LexicalScanner.tokens.get(LexicalScanner.tokens.size() - 1));
            
            if (prevState != 0)
                return LexicalScannerStatus.UNCLOSED_COMMENT;
            
            return LexicalScannerStatus.SCAN_EOF;
        }
        
        if (!codeSnippet.endsWith("\n")) // The automaton requires to know if there's a \n in some cases
            codeSnippet += "\n";
        
        // System.out.println("Received data: " + code); // Debug

        // int previousTotalTokens = LexicalScanner.tokens.size();
        int status = LexicalScannerStatus.SCAN_SUCCESS;
        prevState = LexicalScanner.scan(codeSnippet, lineNumber, prevState);

        if (LexicalScannerStatus.isError(prevState))
            status = prevState;
        
        // for (; previousTotalTokens < LexicalScanner.tokens.size(); previousTotalTokens++)
        //     System.out.println(LexicalScanner.tokens.get(previousTotalTokens));
       
        return status;
    }
    
    /**
     * Given a line of code, this function executes it
     * 
     * @param codeSnippet the code to run
     * @return the status code
     */
    public static int execute(String codeSnippet) {
        return 0;
    }
    
    /**
     * Given a file path, this function runs all the code within.
     * @param filePath the path
     * @return the status exit code
     */
    public static int executeFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists())
            return FILE_NOT_FOUND;

        int exitCode = LexicalScannerStatus.RUN_SUCCESS;
        int lineNumber = 1;
        String fileLine;
        
        // This notation is called try-with-resources, there's no need to manually
        // open/close whatever resource we are accessing
        //
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            while (true) {
                fileLine = br.readLine();
//                if (fileLine == null)
//                    break;

                exitCode = generateTokens(fileLine, lineNumber);
                
                if (exitCode == LexicalScannerStatus.SCAN_EOF) {
                    exitCode = LexicalScannerStatus.RUN_SUCCESS;
                    // System.out.println(LexicalScanner.tokens); // debug
                    
                    break;
                } else if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(fileLine, lineNumber, exitCode);
                    break;
                }
                
                code.add(fileLine);
                lineNumber++;
            }
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (exitCode == LexicalScannerStatus.RUN_SUCCESS) {
            try {
                SyntacticAnalyzer.PROGRAM();
            } catch (IllegalStateException ex) {
                printError(SyntacticAnalyzerStatus.SYNTAX_ERROR);
                return SyntacticAnalyzerStatus.SYNTAX_ERROR;
            }
        
            for (String c : code)
                execute(c);
        }

        return exitCode;
    }
    
    /**
     * REPL mode, this function waits for user input and then executes it.
     * @return the status exit code
     */
    public static int repl() {
        showPrevNext = false;
        int exitCode = LexicalScannerStatus.RUN_SUCCESS;
        int lineNumber = 1;
        
        System.out.println(ESCOMSOLE_VERSION + ". Press CTRL + Z to exit.");
        String fileLine;
        
        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                System.out.print(">>> ");
                
                try {
                    fileLine = s.nextLine();
                } catch (NoSuchElementException e) {
                    System.out.println();
                    fileLine = null;
                }
                
                if (fileLine == null || fileLine.isEmpty())
                    fileLine = null;
                
                exitCode = generateTokens(fileLine, lineNumber);
                
                if (exitCode == LexicalScannerStatus.SCAN_EOF) {
                    exitCode = LexicalScannerStatus.RUN_SUCCESS;
                    break;
                } else if (exitCode != 0) {
                    // System.err.println("error " + exitCode); // Debug
                    printError(fileLine, lineNumber, exitCode);
                    continue;
                }
                
                code.add(fileLine);
                
                
                try {
                    SyntacticAnalyzer.PROGRAM();
                    SyntacticAnalyzer.setAnalysisTokenIndex(LexicalScanner.tokens.size());
                } catch (IllegalStateException ex) {
//                    ex.printStackTrace();
                    printError(SyntacticAnalyzerStatus.SYNTAX_ERROR);
                    SyntacticAnalyzer.setAnalysisTokenIndex(LexicalScanner.tokens.size());
                    code.remove(code.size() - 1);
                    continue;
                }

                execute(fileLine);
                lineNumber++;
            }
        }

        return exitCode;
    }

    private static void printError(String line, int fileLine, int errorCode) {
        String data;

        switch (errorCode) {
            case LexicalScannerStatus.MALFORMED_NUMBER:
                data = "A number is malformed";
            break;
            case LexicalScannerStatus.INVALID_STRING:
                data = "Unterminated string literal";
            break;
            case LexicalScannerStatus.INVALID_CHAR:
                data = "Invalid character";
            break;
            case LexicalScannerStatus.UNCLOSED_COMMENT:
                System.err.println("Error: unclosed comment");
            return;
            default:
                data = "Error";
            break;
        }
        
        String strFileLine = "" + fileLine;
        data += " in line " + strFileLine;
        
        String marker = "  ";
        for (int i = 0; i < strFileLine.length(); i++)
            marker += " ";

        marker += "----";
        
        for (int i = 0; i < LexicalScanner.scanIndex; i++)
            marker += "-";
        
        marker += "^";
        
        System.err.println(String.format("%s\n  %s    %s\n%s", data, strFileLine, line, marker));
    }
    
    private static void printError(int errorCode) {
        Token [] foundExpected = SyntacticAnalyzer.getFoundAndExpectedToken();
        int fileLine = foundExpected[0].getLine();
        fileLine = fileLine > -1 ? fileLine : code.size();
        
        String line = code.get(fileLine - 1);
        
        String data = "Error in line " + fileLine;
        
        if (errorCode == SyntacticAnalyzerStatus.SYNTAX_ERROR) {
            String exp = foundExpected[1].getStrType();
            String foo = foundExpected[0].getStrType();
            
            data = "Syntax error in line " + fileLine;
            if (!exp.equals("") && !exp.equals(foo))
                data += String.format(", expected '%s' but found '%s'", exp, foo);
            else
                data += String.format(" near '%s'", foo);
        }
        
        System.err.println(String.format("%s", data));
        if (showPrevNext && fileLine - 1 > 0)
            System.err.println(String.format("  %d    %s", fileLine - 1, code.get(fileLine - 2)));
        
        System.err.println(String.format("  %d    %s", fileLine, line));
        
        if (showPrevNext && fileLine < code.size())
            System.err.println(String.format("  %d    %s", fileLine + 1, code.get(fileLine)));
    }
}
