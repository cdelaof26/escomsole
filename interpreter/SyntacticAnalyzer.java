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
    
    public static void PROGRAM() throws IllegalStateException {
        DECLARATION();
    }

    private static void DECLARATION() {
        ArrayList<Object> firstFUN_DECL = SyntacticGrammar.first(NonTerminal.FUN_DECL);
        ArrayList<Object> firstVAR_DECL = SyntacticGrammar.first(NonTerminal.VAR_DECL);
        ArrayList<Object> firstSTATEMENT = SyntacticGrammar.first(NonTerminal.STATEMENT);
        
        if (firstFUN_DECL.contains(getCurrentTokenType())) {
            FUN_DECL();
            DECLARATION();
        } else if (firstVAR_DECL.contains(getCurrentTokenType())) {
            VAR_DECL();
            DECLARATION();
        } else if (firstSTATEMENT.contains(getCurrentTokenType())) {
            STATEMENT();
            DECLARATION();
        }
        
        // Can be epsilon
    }

    private static void FUN_DECL() {
        match(TokenType.ESC_FUN);
        match(TokenType.ESC_IDENTIFIER);
        match(TokenType.ESC_LEFT_PAREN);
        PARAMETERS();
        match(TokenType.ESC_RIGHT_PAREN);
        BLOCK();
    }

    private static void VAR_DECL() {
        match(TokenType.ESC_VAR);
        match(TokenType.ESC_IDENTIFIER);
        VAR_INIT();
        match(TokenType.ESC_SEMICOLON);
    }

    private static void VAR_INIT() {
        if (getCurrentTokenType() == TokenType.ESC_EQUAL) {
            match(TokenType.ESC_EQUAL);
            EXPRESSION();
        }
        
        // Can be epsilon
    }

    private static void STATEMENT() {
        ArrayList<Object> firstEXPR_STMT = SyntacticGrammar.first(NonTerminal.EXPR_STMT);
        ArrayList<Object> firstFOR_STMT = SyntacticGrammar.first(NonTerminal.FOR_STMT);
        ArrayList<Object> firstIF_STMT = SyntacticGrammar.first(NonTerminal.IF_STMT);
        ArrayList<Object> firstPRINT_STMT = SyntacticGrammar.first(NonTerminal.PRINT_STMT);
        ArrayList<Object> firstRETURN_STMT = SyntacticGrammar.first(NonTerminal.RETURN_STMT);
        ArrayList<Object> firstWHILE_STMT = SyntacticGrammar.first(NonTerminal.WHILE_STMT);
        ArrayList<Object> firstBLOCK = SyntacticGrammar.first(NonTerminal.BLOCK);
        
        if (firstEXPR_STMT.contains(getCurrentTokenType())) {
            EXPR_STMT();
        } else if (firstFOR_STMT.contains(getCurrentTokenType())) {
            FOR_STMT();
        } else if (firstIF_STMT.contains(getCurrentTokenType())) {
            IF_STMT();
        } else if (firstPRINT_STMT.contains(getCurrentTokenType())) {
            PRINT_STMT();
        } else if (firstRETURN_STMT.contains(getCurrentTokenType())) {
            RETURN_STMT();
        } else if (firstWHILE_STMT.contains(getCurrentTokenType())) {
            WHILE_STMT();
        } else if (firstBLOCK.contains(getCurrentTokenType())) {
            BLOCK();
        } else {
            // Cannot be epsilon
            expected = TokenType.STATEMENT;
            throwError();
        }
    }

    private static void EXPR_STMT() {
        EXPRESSION();
        match(TokenType.ESC_SEMICOLON);
    }

    private static void FOR_STMT() {
        match(TokenType.ESC_FOR);
        match(TokenType.ESC_LEFT_PAREN);
        FOR_STMT_INIT();
        FOR_STMT_COND();
        FOR_STMT_INC();
        match(TokenType.ESC_RIGHT_PAREN);
        STATEMENT();
    }

    private static void FOR_STMT_INIT() {
        ArrayList<Object> firstVAR_DECL = SyntacticGrammar.first(NonTerminal.VAR_DECL);
        ArrayList<Object> firstEXPR_STMT = SyntacticGrammar.first(NonTerminal.EXPR_STMT);
        
        if (firstVAR_DECL.contains(getCurrentTokenType())) {
            VAR_DECL();
        } else if (firstEXPR_STMT.contains(getCurrentTokenType())) {
            EXPR_STMT();
        } else {
            match(TokenType.ESC_SEMICOLON);
        }
    }

    private static void FOR_STMT_COND() {
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        
        if (firstEXPRESSION.contains(getCurrentTokenType())) {
            EXPRESSION();
            match(TokenType.ESC_SEMICOLON);
        } else {
            match(TokenType.ESC_SEMICOLON);
        }
    }

    private static void FOR_STMT_INC() {
        ArrayList<Object> firstFOR_STMT_INC = SyntacticGrammar.first(NonTerminal.FOR_STMT_INC);
        if (firstFOR_STMT_INC.contains(getCurrentTokenType()))
            EXPRESSION();
        
        // Can be epsilon
    }

    private static void IF_STMT() {
        match(TokenType.ESC_IF);
        match(TokenType.ESC_LEFT_PAREN);
        EXPRESSION();
        match(TokenType.ESC_RIGHT_PAREN);
        STATEMENT();
        ELSE_STATEMENT();
    }

    private static void ELSE_STATEMENT() {
        if (getCurrentTokenType() == TokenType.ESC_ELSE) {
            match(TokenType.ESC_ELSE);
            STATEMENT();
        }
        
        // Can be epsilon
    }

    private static void PRINT_STMT() {
        match(TokenType.ESC_PRINT);
        EXPRESSION();
        match(TokenType.ESC_SEMICOLON);
    }

    private static void RETURN_STMT() {
        match(TokenType.ESC_RETURN);
        RETURN_EXP_OPC();
        match(TokenType.ESC_SEMICOLON);
    }

    private static void RETURN_EXP_OPC() {
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        if (firstEXPRESSION.contains(getCurrentTokenType()))
            EXPRESSION();
        
        // Can be epsilon
    }

    private static void WHILE_STMT() {
        match(TokenType.ESC_WHILE);
        match(TokenType.ESC_LEFT_PAREN);
        EXPRESSION();
        match(TokenType.ESC_RIGHT_PAREN);
        STATEMENT();
    }

    private static void BLOCK() {
        match(TokenType.ESC_LEFT_BRACE);
        DECLARATION();
        match(TokenType.ESC_RIGHT_BRACE);
    }

    private static void EXPRESSION() {
        ASSIGNMENT();
    }

    private static void ASSIGNMENT() {
        LOGIC_OR();
        ASSIGNMENT_OPC();
    }

    private static void ASSIGNMENT_OPC() {
        switch (getCurrentTokenType()) {
            case ESC_EQUAL:
            case ESC_STAR_EQUAL:
            case ESC_SLASH_EQUAL:
            case ESC_PLUS_EQUAL:
            case ESC_MINUS_EQUAL:
                match(getCurrentTokenType());
                EXPRESSION();
            break;
        }
        
        // Can be epsilon
    }

    private static void LOGIC_OR() {
        LOGIC_AND();
        LOGIC_OR_P();
    }

    private static void LOGIC_OR_P() {
        if (getCurrentTokenType() == TokenType.ESC_OR) {
            match(TokenType.ESC_OR);
            LOGIC_OR();
        }
        
        // Can be epsilon
    }

    private static void LOGIC_AND() {
        EQUALITY();
        LOGIC_AND_P();
    }

    private static void LOGIC_AND_P() {
        if (getCurrentTokenType() == TokenType.ESC_AND) {
            match(TokenType.ESC_AND);
            LOGIC_AND();
        }
    }

    private static void EQUALITY() {
        COMPARISON();
        EQUALITY_P();
    }

    private static void EQUALITY_P() {
        switch (getCurrentTokenType()) {
            case ESC_NOT_EQUAL:
            case ESC_EQUAL_EQUAL:
                match(getCurrentTokenType());
                EQUALITY();
            break;
        }
        
        // Can be epsilon
    }

    private static void COMPARISON() {
        TERM();
        COMPARISON_P();
    }

    private static void COMPARISON_P() {
        switch (getCurrentTokenType()) {
            case ESC_GREATER:
            case ESC_GREATER_EQUAL:
            case ESC_LESS:
            case ESC_LESS_EQUAL:
                match(getCurrentTokenType());
                COMPARISON();
            break;
        }
        
        // Can be epsilon
    }

    private static void TERM() {
        FACTOR();
        TERM_P();
    }

    private static void TERM_P() {
        switch (getCurrentTokenType()) {
            case ESC_MINUS:
            case ESC_PLUS:
                match(getCurrentTokenType());
                TERM();
            break;
            
            case ESC_MINUS_MINUS:
            case ESC_PLUS_PLUS:
                match(getCurrentTokenType());
            break;
        }
        
        // Can be epsilon
    }

    private static void FACTOR() {
        UNARY();
        FACTOR_P();
    }

    private static void FACTOR_P() {
        switch (getCurrentTokenType()) {
            case ESC_SLASH:
            case ESC_STAR:
                match(getCurrentTokenType());
                FACTOR();
            break;
        }
        
        // Can be epsilon
    }

    private static void UNARY() {
        switch (getCurrentTokenType()) {
            case ESC_NOT:
            case ESC_MINUS:
                match(getCurrentTokenType());
                UNARY();
            break;
            
            default:
                CALL();
            break;
        }
    }

    private static void CALL() {
        PRIMARY();
        CALL_P();
    }

    private static void CALL_P() {
        switch (getCurrentTokenType()) {
            case ESC_LEFT_PAREN:
                match(getCurrentTokenType());
                ARGUMENTS();
                match(TokenType.ESC_RIGHT_PAREN);
            break;
            case ESC_QUESTION_MARK:
                match(getCurrentTokenType());
                EXPRESSION();
                match(TokenType.ESC_COLON);
                EXPRESSION();
            break;
            default:
                ArrayList<Object> firstARRAY = SyntacticGrammar.first(NonTerminal.ARRAY);
                if (firstARRAY.contains(getCurrentTokenType()))
                    ARRAY();
            break;
        }
        
        // Can be epsilon
    }

    private static void PRIMARY() {
        switch (getCurrentTokenType()) {
            case ESC_TRUE:
            case ESC_FALSE:
            case ESC_NULL:
            case ESC_NUMBER:
            case ESC_FLOATING_NUMBER:
            case ESC_DOUBLE_NUMBER:
            case ESC_STRING:
            case ESC_IDENTIFIER:
                match(getCurrentTokenType());
            break;
            
            case ESC_LEFT_PAREN:
                match(getCurrentTokenType());
                EXPRESSION();
                match(TokenType.ESC_RIGHT_PAREN);
            break;
            
            default:
                ArrayList<Object> firstARRAY = SyntacticGrammar.first(NonTerminal.ARRAY);
                if (firstARRAY.contains(getCurrentTokenType())) {
                    ARRAY();
                } else {
                    expected = TokenType.NONE;
                    throwError();
                }
            break;
        }
    }

    private static void ARRAY() {
        match(TokenType.ESC_LEFT_BRACKET);
        ARGUMENTS();
        match(TokenType.ESC_RIGHT_BRACKET);
    }

    private static void PARAMETERS() {
        if (getCurrentTokenType() == TokenType.ESC_IDENTIFIER) {
            match(getCurrentTokenType());
            PARAMETERS_P();
        }
        
        // Can be epsilon
    }

    private static void PARAMETERS_P() {
        if (getCurrentTokenType() == TokenType.ESC_COMMA) {
            match(getCurrentTokenType());
            match(TokenType.ESC_IDENTIFIER);
            PARAMETERS_P();
        }
        
        // Can be epsilon
    }

    private static void ARGUMENTS() {
        ArrayList<Object> firstEXPRESSION = SyntacticGrammar.first(NonTerminal.EXPRESSION);
        if (firstEXPRESSION.contains(getCurrentTokenType())) {
            EXPRESSION();
            ARGUMENTS_P();
        }

        // Can be epsilon
    }

    private static void ARGUMENTS_P() {
        if (getCurrentTokenType() == TokenType.ESC_COMMA) {
            match(getCurrentTokenType());
            EXPRESSION();
            ARGUMENTS_P();
        }
        
        // Can be epslion
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
