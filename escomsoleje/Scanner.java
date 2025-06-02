package escomsoleje;

import interpreter.Interpreter;
import java.io.File;

public class Scanner {
  public static void main(String[] args) {
    if (args.length == 0) 
      System.exit(Interpreter.repl());

    if (args.length == 1) {
      int status = Interpreter.executeFile(args[0]);
      
      if (status == Interpreter.FILE_NOT_FOUND)
        System.err.println(String.format("File '%s' not found\n", new File(args[0]).getAbsolutePath()));

      System.exit(status);
    }
    
    System.err.println("Usage:\n\tREPL:\t\tjava Scanner\n\tRun file:\tjava Scanner path/to/file");

    System.exit(Interpreter.INVALID_SCANNER_CALL);
  } 
}
