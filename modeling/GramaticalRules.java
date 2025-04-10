package modeling;
import java.util.HashMap;

public class GramaticalRules{
    public static HashMap<NoTerminales, Object[][]> Rules;
    static {
        Rules = new HashMap<>();
        Rules.put(NoTerminales.PROGRAM, new Object[][]{
            {NoTerminales.DECLARATION}      
        });
        Rules.put(NoTerminales.DECLARATION, new Object[][]{
            {NoTerminales.FUN_DECL, NoTerminales.DECLARATION},
            {NoTerminales.VAR_DECL, NoTerminales.DECLARATION},
            {NoTerminales.STATEMENT, NoTerminales.DECLARATION},
            null
        });
        
    }
}
