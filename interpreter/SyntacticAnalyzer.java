package interpreter;

import java.util.ArrayList;
import modeling.NonTerminal;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class SyntacticAnalyzer {
    private static int analysisTokenIndex = 0;
    
    private static void throwError() throws IllegalStateException {
        throw new IllegalStateException(String.format("Syntax error; token #%d: %s", analysisTokenIndex, LexicalScanner.tokens.get(analysisTokenIndex)));
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
            throw new IllegalArgumentException("d must be TokenType or NonTerminal");
        
        switch ((NonTerminal) d) {
            case DECLARATION:    DECLARATION();    break;
            case FUN_DECL:       FUN_DECL();       break;
            case VAR_DECL:       VAR_DECL();       break;
            case VAR_INIT:       VAR_INIT();       break;
            case STATEMENT:      STATEMENT();      break;
            case EXPR_STMT:      EXPR_STMT();      break;
            case FOR_STMT:       FOR_STMT();       break;
            case FOR_STMT_INIT:  FOR_STMT_INIT();  break;
            case FOR_STMT_COND:  FOR_STMT_COND();  break;
            case FOR_STMT_INC:   FOR_STMT_INC();   break;
            case IF_STMT:        IF_STMT();        break;
            case ELSE_STATEMENT: ELSE_STATEMENT(); break;
            case PRINT_STMT:     PRINT_STMT();     break;
            case RETURN_STMT:    RETURN_STMT();    break;
            case RETURN_EXP_OPC: RETURN_EXP_OPC(); break;
            case WHILE_STMT:     WHILE_STMT();     break;
            case BLOCK:          BLOCK();          break;
            case EXPRESSION:     EXPRESSION();     break;
            case ASSIGNMENT:     ASSIGNMENT();     break;
            case ASSIGNMENT_OPC: ASSIGNMENT_OPC(); break;
            case LOGIC_OR:       LOGIC_OR();       break;
            case LOGIC_OR_P:     LOGIC_OR_P();     break;
            case LOGIC_AND:      LOGIC_AND();      break;
            case LOGIC_AND_P:    LOGIC_AND_P();    break;
            case EQUALITY:       EQUALITY();       break;
            case EQUALITY_P:     EQUALITY_P();     break;
            case COMPARISON:     COMPARISON();     break;
            case COMPARISON_P:   COMPARISON_P();   break;
            case TERM:           TERM();           break;
            case TERM_P:         TERM_P();         break;
            case FACTOR:         FACTOR();         break;
            case FACTOR_P:       FACTOR_P();       break;
            case UNARY:          UNARY();          break;
            case CALL:           CALL();           break;
            
            case ARRAY:          ARRAY();          break;
            
            case CALL_P:         CALL_P();         break;
            case PRIMARY:        PRIMARY();        break;
            case PARAMETERS:     PARAMETERS();     break;
            case PARAMETERS_P:   PARAMETERS_P();   break;
            case ARGUMENTS:      ARGUMENTS();      break;
            case ARGUMENTS_P:    ARGUMENTS_P();    break;
            
            default: throwError();
        }
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
        DECLARATION();
    }
    
    private static void DECLARATION() throws IllegalStateException {
        findAppropriateProductionWrap(NonTerminal.DECLARATION);
    }
    
    private static void FUN_DECL() {
        switch (getCurrentTokenType()) {
            case ESC_FUN:
                match(TokenType.ESC_FUN);
                match(TokenType.ESC_IDENTIFIER);
                match(TokenType.ESC_LEFT_PAREN);
                PARAMETERS();
                match(TokenType.ESC_RIGHT_PAREN);
                BLOCK();
            break;
            default: throwError();
        }
    }

    private static void VAR_DECL() {
        switch (getCurrentTokenType()) {
            case ESC_VAR:
                match(TokenType.ESC_VAR);
                match(TokenType.ESC_IDENTIFIER);
                VAR_INIT();
                match(TokenType.ESC_SEMICOLON);
            break;
            default: throwError();
        }
    }

    private static void VAR_INIT() {
        switch (getCurrentTokenType()) {
            case ESC_EQUAL:
                match(TokenType.ESC_EQUAL);
                EXPRESSION();
            break;
        }
    }

    private static void STATEMENT() {
        findAppropriateProductionWrap(NonTerminal.STATEMENT);
    }

    private static void EXPR_STMT() {
        EXPRESSION();
        match(TokenType.ESC_SEMICOLON);
    }

    private static void FOR_STMT() {
        switch (getCurrentTokenType()) {
            case ESC_FOR:
                match(TokenType.ESC_FOR);
                match(TokenType.ESC_LEFT_PAREN);
                FOR_STMT_INIT();
                FOR_STMT_COND();
                FOR_STMT_INC();
                match(TokenType.ESC_RIGHT_PAREN);
                STATEMENT();
            break;
            default: throwError();
        }
    }

    private static void FOR_STMT_INIT() {
        findAppropriateProductionWrap(NonTerminal.FOR_STMT_INIT);
    }

    private static void FOR_STMT_COND() {
        findAppropriateProductionWrap(NonTerminal.FOR_STMT_COND);
    }

    private static void FOR_STMT_INC() {
        findAppropriateProductionWrap(NonTerminal.FOR_STMT_INC);
    }

    private static void IF_STMT() {
        switch (getCurrentTokenType()) {
            case ESC_IF:
                match(TokenType.ESC_IF);
                match(TokenType.ESC_LEFT_PAREN);
                EXPRESSION();
                match(TokenType.ESC_RIGHT_PAREN);
                STATEMENT();
                ELSE_STATEMENT();
            break;
            default: throwError();
        }
    }

    private static void ELSE_STATEMENT() {
        findAppropriateProductionWrap(NonTerminal.ELSE_STATEMENT);
    }

    private static void PRINT_STMT() {
        switch (getCurrentTokenType()) {
            case ESC_PRINT:
                match(TokenType.ESC_PRINT);
                EXPRESSION();
                match(TokenType.ESC_SEMICOLON);
            break;
            default: throwError();
        }
    }

    private static void RETURN_STMT() {
        switch (getCurrentTokenType()) {
            case ESC_RETURN:
                match(TokenType.ESC_RETURN);
                RETURN_EXP_OPC();
                match(TokenType.ESC_SEMICOLON);
            break;
            default: throwError();
        }
    }

    private static void RETURN_EXP_OPC() {
        findAppropriateProductionWrap(NonTerminal.RETURN_EXP_OPC);
    }

    private static void WHILE_STMT() {
        switch (getCurrentTokenType()) {
            case ESC_WHILE:
                match(TokenType.ESC_WHILE);
                match(TokenType.ESC_LEFT_PAREN);
                EXPRESSION();
                match(TokenType.ESC_RIGHT_PAREN);
                STATEMENT();
            break;
            default: throwError();
        }
    }

    private static void BLOCK() {
        switch (getCurrentTokenType()) {
            case ESC_LEFT_BRACE:
                match(TokenType.ESC_LEFT_BRACE);
                DECLARATION();
                match(TokenType.ESC_RIGHT_BRACE);
            break;
            default: throwError();
        }
    }

    private static void EXPRESSION() {
        ASSIGNMENT();
    }

    private static void ASSIGNMENT() {
        LOGIC_OR();
        ASSIGNMENT_OPC();
    }

    private static void ASSIGNMENT_OPC() {
        findAppropriateProductionWrap(NonTerminal.ASSIGNMENT_OPC);
    }

    private static void LOGIC_OR() {
        LOGIC_AND();
        LOGIC_OR_P();
    }

    private static void LOGIC_OR_P() {
        findAppropriateProductionWrap(NonTerminal.LOGIC_OR_P);
    }

    private static void LOGIC_AND() {
        EQUALITY();
        LOGIC_AND_P();
    }

    private static void LOGIC_AND_P() {
        findAppropriateProductionWrap(NonTerminal.LOGIC_AND_P);
    }

    private static void EQUALITY() {
        COMPARISON();
        EQUALITY_P();
    }

    private static void EQUALITY_P() {
        findAppropriateProductionWrap(NonTerminal.EQUALITY_P);
    }

    private static void COMPARISON() {
        TERM();
        COMPARISON_P();
    }

    private static void COMPARISON_P() {
        findAppropriateProductionWrap(NonTerminal.COMPARISON_P);
    }

    private static void TERM() {
        FACTOR();
        TERM_P();
    }

    private static void TERM_P() {
        findAppropriateProductionWrap(NonTerminal.TERM_P);
    }

    private static void FACTOR() {
        UNARY();
        FACTOR_P();
    }

    private static void FACTOR_P() {
        findAppropriateProductionWrap(NonTerminal.FACTOR_P);
    }

    private static void UNARY() {
        findAppropriateProductionWrap(NonTerminal.UNARY);
    }

    private static void CALL() {
        PRIMARY();
        CALL_P();
    }
    
    
    private static void ARRAY() {
        switch (getCurrentTokenType()) {
            case ESC_LEFT_BRACKET:
                match(TokenType.ESC_LEFT_BRACKET);
                ARGUMENTS();
                match(TokenType.ESC_RIGHT_BRACKET);
            break;
            default: throwError();
        }
    }
    
    
    private static void CALL_P() {
        findAppropriateProductionWrap(NonTerminal.CALL_P);
    }

    private static void PRIMARY() {
        findAppropriateProductionWrap(NonTerminal.PRIMARY);
    }

    private static void PARAMETERS() {
        findAppropriateProductionWrap(NonTerminal.PARAMETERS);
    }

    private static void PARAMETERS_P() {
        findAppropriateProductionWrap(NonTerminal.PARAMETERS_P);
    }

    private static void ARGUMENTS() {
        findAppropriateProductionWrap(NonTerminal.ARGUMENTS);
    }

    private static void ARGUMENTS_P() {
        findAppropriateProductionWrap(NonTerminal.ARGUMENTS_P);
    }
    
    /**
     * Retrieves the TokenType of the token being analyzed
     * @return the token
     */
    private static TokenType getCurrentTokenType() {
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
        
        throwError();
    }
}
