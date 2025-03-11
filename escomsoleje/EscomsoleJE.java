package escomsoleje;

import interpreter.Interpreter;
import interpreter.Status;
import java.io.File;

/**
 * Entry point
 * @author cristopher
 */
public class EscomsoleJE {
    public static void main(String[] args) {
        if (args.length == 0) // No user provided arguments
            System.exit(Interpreter.repl());
        
        if (args.length == 1) { // One user provided argument
            int status = Interpreter.executeFile(args[0]);
            if (status == Status.FILE_NOT_FOUND)
                System.err.println(String.format("File '%s' not found\n", new File(args[0]).getAbsolutePath()));

            System.exit(status);
        }
        
        System.err.println("Usage:\n  java escomsole : REPL mode\n  java escomsole path/to/file : execute file\n");
        
        System.exit(Status.INVALID_ESCOMSOLE_CALL);
    }
}
