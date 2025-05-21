package escomsoleje;

import java.util.List;
import modeling.NoTerminales;
import modeling.TokenType;
import parser.First;

/**
 * Entry point
 * @author cristopher
 */
public class EscomsoleJE {
    public static void main(String[] args) {
        /*if (args.length == 0) // No user provided arguments
            System.exit(Interpreter.repl());
        
        if (args.length == 1) { // One user provided argument
            int status = Interpreter.executeFile(args[0]);
            if (status == Interpreter.FILE_NOT_FOUND)
                System.err.println(String.format("File '%s' not found\n", new File(args[0]).getAbsolutePath()));

            System.exit(status);
        }
        
        System.err.println("Usage:\n\tREPL:\t\tjava EscomsoleJE\n\tRun file:\tjava EscomsoleJE path/to/file");
        
        System.exit(Interpreter.INVALID_ESCOMSOLE_CALL);
        */
        List<TokenType> A = First.TheFirst(NoTerminales.WHILE_STMT);
        System.out.println(A.size());
        System.out.println(A);
    }
}
