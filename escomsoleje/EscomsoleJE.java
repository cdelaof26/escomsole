package escomsoleje;

import interpreter.Interpreter;
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
            if (status == Interpreter.FILE_NOT_FOUND)
                System.err.println(String.format("File '%s' not found\n", new File(args[0]).getAbsolutePath()));
            
//            if (status == 0)
//                System.out.println("Valid program");
            
            System.exit(status);
        }
        
        System.err.println("Usage:\n\tREPL:\t\tjava EscomsoleJE\n\tRun file:\tjava EscomsoleJE path/to/file");
        
        System.exit(Interpreter.INVALID_ESCOMSOLE_CALL);
    }
}
