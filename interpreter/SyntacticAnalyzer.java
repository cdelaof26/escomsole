package interpreter;

import java.util.ArrayList;
import modeling.NonTerminal;
import modeling.Token;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class SyntacticAnalyzer {
    private static int analysisTokenIndex = 0;
    private static TokenType expected = TokenType.NONE;
    
    private static void throwError() throws IllegalStateException {
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            throw new IllegalStateException("Syntax error - missing tokens");
        
        throw new IllegalStateException(String.format("Syntax error; token #%d: %s", analysisTokenIndex, LexicalScanner.tokens.get(analysisTokenIndex)));
    }

    public static Token [] getFoundAndExpectedToken() {
        Token t;
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            t = new Token(TokenType.NONE, -1);
        else
            t = LexicalScanner.tokens.get(analysisTokenIndex);

        return new Token[] {
            t, new Token(expected, t.getLine())
        };
    }

    public static Token getNextToken() {
        if (analysisTokenIndex + 1 >= LexicalScanner.tokens.size())
            return new Token(TokenType.NONE, -1);

        return LexicalScanner.tokens.get(analysisTokenIndex + 1);
    }

    public static void setAnalysisTokenIndex(int analysisTokenIndex) {
        SyntacticAnalyzer.analysisTokenIndex = analysisTokenIndex;
    }
    
    /**
     * This function checks if an object d is a TokenType or a NonTerminal.<br>
     * In case it's a TokenType, calls match, otherwise will call the function 
     * associated to the NonTerminal.
     * 
     * @param d the object to evaluate
     */
    private static void productionRuleExecution(Object d) {
        if (d instanceof TokenType) {
            match((TokenType) d);
            return;
        }
        
        if (!(d instanceof NonTerminal)) // This shouldn't happen
            throw new IllegalArgumentException("d must be either TokenType or NonTerminal");
        
        findAppropriateProductionWrap((NonTerminal) d);
    }
    
    /**
     * Given a NonTerminal, this function finds in the associated rule a 
     * production body. This production body starts with the current token.
     * 
     * @param nt the NonTerminal
     * @return the production body or null if the rule allows epsilon
     * @throws IllegalStateException if the rule doesn't allow epsilon and no 
     * production body starts with the current token type
     * @see SyntacticAnalyzer#getCurrentTokenType() 
     * @see SyntacticGrammar#first(java.lang.Object, java.lang.Object)
     */
    private static Object [] findAppropriateProduction(NonTerminal nt) {
        Object [][] rules = SyntacticGrammar.rules.get(nt);
        
        int i = 0;
        boolean foundProduction = false;
        boolean canBeEpsilon = rules[rules.length - 1] == null;
        int l = !canBeEpsilon ? rules.length : rules.length - 1;
        
        for (; i < l && !foundProduction; i++) {
            // It's not necessary to check every element in the production, only the first
            //
            ArrayList<Object> firstSet = SyntacticGrammar.first(rules[i][0], null);

            if (firstSet.indexOf(getCurrentTokenType()) != -1) {
                foundProduction = true;
                break;
            }
        }
        
        if (!foundProduction) {
            if (canBeEpsilon)
                return null;
            
            expected = TokenType.NONE;
            throwError();
        }
        
        return rules[i];
    }
    
    /**
     * Given a NonTerminal, this function finds a production body that fits 
     * the current token type and then performs all the matches and calls 
     * associated to that production body
     * 
     * @param nt the NonTerminal
     */
    private static void findAppropriateProductionWrap(NonTerminal nt) {
        Object [] production = findAppropriateProduction(nt);
        
        // In some cases, it can be epsilon and it's not allowed, 
        // however findAppropriateProduction will throw an error if it couldn't 
        // find an production body that matches the current token
        if (production == null)
            return;
        
        for (Object pe : production)
            productionRuleExecution(pe);
    }
    
    public static void PROGRAM() throws IllegalStateException {
        findAppropriateProductionWrap(NonTerminal.DECLARATION);
    }
    
    /**
     * Retrieves the TokenType of the token being analyzed
     * @return the token
     */
    public static TokenType getCurrentTokenType() {
        if (analysisTokenIndex >= LexicalScanner.tokens.size())
            return TokenType.NONE;
        return LexicalScanner.tokens.get(analysisTokenIndex).getType();
    }
    
    /**
     * Given a t TokenType, this function checks if the current token is equal
     * @param t the TokenType to compare current TokenType against
     * @throws IllegalStateException if the tokens don't match
     */
    private static void match(TokenType t) throws IllegalStateException {
        if (t == getCurrentTokenType()) {
            analysisTokenIndex++;
            return;
        }
        
        expected = t;
        
        throwError();
    }
}
