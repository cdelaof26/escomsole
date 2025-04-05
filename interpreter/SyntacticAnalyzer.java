package interpreter;

import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class SyntacticAnalyzer {
    private static int analysisTokenIndex = 0;
    
    
    private static void match(TokenType t) throws IllegalStateException {
        if (t == LexicalScanner.tokens.get(analysisTokenIndex).getType()) {
            analysisTokenIndex++;
            return;
        }
        
        throw new IllegalStateException("Syntax error");
    }
}
