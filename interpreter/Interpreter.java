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
import parser.SyntacticAnalyzer;
import parser.SyntacticAnalyzerStatus;

public class Interpreter {
  public static final String SCANNER_VERSION = "scanner v0.0.4-1 (Mayo 1 2025)";
  
  public static final int FILE_NOT_FOUND = 1;
  public static final int INVALID_SCANNER_CALL = -1;

  private static int prevState = 0;

  private static final ArrayList<String> code = new ArrayList<>();
  private static boolean showPrevNext = true;

  private static int generateTokens(String codeSnippet, int lineNumber) {
    if (codeSnippet == null) {
      LexicalScanner.tokens.add(new Token(TokenType.ESC_EOF, "$", -1, -1));

      if (prevState != 0)
        return LexicalScannerStatus.UNCLOSED_COMMENT;

      return LexicalScannerStatus.SCAN_EOF;
    }

    if (!codeSnippet.endsWith("\n"))
      codeSnippet += "\n";

    int status = LexicalScannerStatus.SCAN_SUCCESS;
    prevState = LexicalScanner.scan(codeSnippet, lineNumber, prevState);

    if (LexicalScannerStatus.isError(prevState))
      status = prevState;

    return status;
  }

  public static int execute(String codeSnippet) {
    return 0;
  }

  public static int executeFile(String filePath) {
    File f = new File(filePath);

    if (!f.exists()) 
      return FILE_NOT_FOUND;

    int exitCode = LexicalScannerStatus.RUN_SUCCESS;
    int lineNumber = 1;
    String fileLine;

    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      while (true) {
        fileLine = br.readLine();

        exitCode = generateTokens(fileLine, lineNumber);

        if (exitCode == LexicalScannerStatus.SCAN_EOF) {
          exitCode = LexicalScannerStatus.RUN_SUCCESS;
          break;
        } else if (exitCode != 0) {
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

    // System.out.println("Parsing..." + LexicalScanner.tokens);

    if (exitCode == LexicalScannerStatus.RUN_SUCCESS) {
      try {
        SyntacticAnalyzer.parse();
      } catch (IllegalStateException e) {
        printError(SyntacticAnalyzerStatus.SYNTAX_ERROR);
        return SyntacticAnalyzerStatus.SYNTAX_ERROR;
      }

      Token token = SyntacticAnalyzer.getCurrentToken();

      if (token == null || token.getType() != TokenType.ESC_EOF) 
        return SyntacticAnalyzerStatus.SYNTAX_ERROR;

      for (String c : code) {
        execute(c);
      }
    }

    return exitCode;
  }

  public static int repl() {
    showPrevNext = false;
    int exitCode = LexicalScannerStatus.RUN_SUCCESS;
    int lineNumber = 1;

    System.out.println(SCANNER_VERSION + ". Press CTRL + Z to exit.");
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
          printError(fileLine, lineNumber, exitCode);
          continue;
        }

        code.add(fileLine);

        try {
          // System.out.println("Parsing..." + LexicalScanner.tokens);
          SyntacticAnalyzer.parse();
        } catch (IllegalStateException ex) {
          printError(SyntacticAnalyzerStatus.SYNTAX_ERROR);
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

    String maker = "";

    for (int i = 0; i < strFileLine.length(); i++)
      maker += " ";

    maker += "----";

    for (int i = 0; i < LexicalScanner.scanIndex; i++)
      maker += "-";

    maker += "^";

    System.err.println(String.format("%s\n %s   %s\n%s", data, strFileLine, line, maker));
  }
  
  private static void printError(int errorCode) {
    Token [] foundExpected = SyntacticAnalyzer.getFoundAndExpectedToken();
    int fileLine = foundExpected[0].getLine();
    fileLine = fileLine > -1 ? fileLine : code.size();
    
    String line = code.get(fileLine - 1);
    
    String data = "Error in line " + fileLine;
    int markerIndex = -1;
    
    if (errorCode == SyntacticAnalyzerStatus.SYNTAX_ERROR) {
    String exp = foundExpected[1].getStrType();
    String foo = foundExpected[0].getRepresentation();
        
    markerIndex = foundExpected[0].getColumn();
        
    data = "Syntax error in line " + fileLine;
  
    if (!exp.equals("") && !exp.equals(foo))
      data += String.format(", expected '%s' but found '%s'", exp, foo);
    else
      data += String.format(" near '%s'", foo);
    } else if (errorCode == SyntacticAnalyzerStatus.ILLEGAL_TERMINATION) {
    Token foo = foundExpected[0];
    markerIndex = foo.getColumn();
        
    if (foo.getType() == TokenType.ESC_RIGHT_BRACE)
      data = "Extraneous closing brace in line " + fileLine;
    else 
      data = "Syntax error in line " + fileLine + String.format(" near '%s'", foo.getRepresentation());
    }
    
    System.err.println(data);
    if (showPrevNext && fileLine - 1 > 0)
      System.err.println(String.format("  %d    %s", fileLine - 1, code.get(fileLine - 2)));
    
    System.err.println(String.format("  %d    %s", fileLine, line));
    if (markerIndex != -1) {
      String marker = "  ";
        
      for (int i = 0; i < ("" + fileLine).length(); i++)
        marker += " ";
        marker += "----";
      for (int i = 0; i < markerIndex; i++)
        marker += "-";

      System.err.println(marker + "^");
    }
    
    if (showPrevNext && fileLine < code.size())
      System.err.println(String.format("  %d    %s", fileLine + 1, code.get(fileLine)));
  }
}
