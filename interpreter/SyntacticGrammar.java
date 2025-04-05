package interpreter;

import java.util.HashMap;
import modeling.NonTerminal;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class SyntacticGrammar {
    public static HashMap<NonTerminal, Object[][]> rules;
    
    static {
        rules.put(NonTerminal.PROGRAM, new Object[][]{{NonTerminal.DECLARATION}});
        
        
        
        
        rules.put(NonTerminal.DECLARATION, new Object[][]{
            {NonTerminal.FUN_DECL, NonTerminal.DECLARATION},
            {NonTerminal.VAR_DECL, NonTerminal.DECLARATION},
            {NonTerminal.STATEMENT, NonTerminal.DECLARATION},
            {}
        });
        
        rules.put(NonTerminal.FUN_DECL, new Object[][]{
            {TokenType.ESC_FUN, TokenType.ESC_IDENTIFIER, TokenType.ESC_LEFT_PAREN, NonTerminal.PARAMETERS, TokenType.ESC_RIGHT_PAREN, NonTerminal.BLOCK}
        });
        
        rules.put(NonTerminal.VAR_DECL, new Object[][]{{TokenType.ESC_VAR, TokenType.ESC_IDENTIFIER, NonTerminal.VAR_INIT, TokenType.ESC_SEMICOLON}});
        
        rules.put(NonTerminal.VAR_INIT, new Object[][]{
            {TokenType.ESC_EQUAL, NonTerminal.EXPRESSION}, 
            {}
        });
        
        
        
        
        rules.put(NonTerminal.STATEMENT, new Object[][]{
            {NonTerminal.EXPR_STMT}, 
            {NonTerminal.FOR_STMT}, 
            {NonTerminal.IF_STMT}, 
            {NonTerminal.PRINT_STMT}, 
            {NonTerminal.RETURN_STMT}, 
            {NonTerminal.WHILE_STMT}, 
            {NonTerminal.BLOCK}
        });
        
        rules.put(NonTerminal.EXPR_STMT, new Object[][] {
            {NonTerminal.EXPRESSION, TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.FOR_STMT, new Object[][] {
            {TokenType.ESC_FOR, TokenType.ESC_LEFT_PAREN, NonTerminal.FOR_STMT_INIT, NonTerminal.FOR_STMT_COND, NonTerminal.FOR_STMT_INC, TokenType.ESC_RIGHT_PAREN, NonTerminal.STATEMENT}
        });
        
        rules.put(NonTerminal.FOR_STMT_INIT, new Object[][] {
            {NonTerminal.VAR_DECL},
            {NonTerminal.EXPR_STMT},
            {TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.FOR_STMT_COND, new Object[][] {
            {NonTerminal.EXPRESSION, TokenType.ESC_SEMICOLON},
            {TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.FOR_STMT_INC, new Object[][] {
            {NonTerminal.EXPRESSION},
            {}
        });
        
        rules.put(NonTerminal.IF_STMT, new Object[][] {
            {TokenType.ESC_IF, TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NonTerminal.STATEMENT, NonTerminal.ELSE_STATEMENT}
        });
        
        rules.put(NonTerminal.ELSE_STATEMENT, new Object[][] {
            {TokenType.ESC_ELSE, NonTerminal.STATEMENT}, 
            {}
        });
        
        rules.put(NonTerminal.PRINT_STMT, new Object[][] {{NonTerminal.EXPRESSION, TokenType.ESC_SEMICOLON}});
        
        rules.put(NonTerminal.RETURN_STMT, new Object[][] {{TokenType.ESC_RETURN, NonTerminal.RETURN_EXP_OPC, TokenType.ESC_SEMICOLON}});
        
        rules.put(NonTerminal.RETURN_EXP_OPC, new Object[][] {
            {NonTerminal.EXPRESSION}, 
            {}
        });
        
        rules.put(NonTerminal.WHILE_STMT, new Object[][] {
            {TokenType.ESC_WHILE, TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NonTerminal.STATEMENT}
        });
        
        rules.put(NonTerminal.BLOCK, new Object[][] {{TokenType.ESC_LEFT_BRACE, NonTerminal.DECLARATION, TokenType.ESC_RIGHT_BRACE}});
        
        
        
        
        rules.put(NonTerminal.EXPRESSION, new Object[][] {{NonTerminal.ASSIGNMENT}});
        
        rules.put(NonTerminal.ASSIGNMENT, new Object[][] {{NonTerminal.LOGIC_OR, NonTerminal.ASSIGNMENT_OPC}});
        
        rules.put(NonTerminal.ASSIGNMENT_OPC, new Object[][] {
            {TokenType.ESC_EQUAL, NonTerminal.EXPRESSION},
            {}
        });
        
        rules.put(NonTerminal.LOGIC_OR, new Object[][] {{NonTerminal.LOGIC_AND, NonTerminal.LOGIC_OR_P}});
        
        rules.put(NonTerminal.LOGIC_OR_P, new Object[][] {
            {TokenType.ESC_OR, NonTerminal.LOGIC_OR},
            {}
        });
        
        rules.put(NonTerminal.LOGIC_AND, new Object[][] {{NonTerminal.EQUALITY, NonTerminal.LOGIC_AND_P}});
        
        rules.put(NonTerminal.LOGIC_AND_P, new Object[][] {
            {TokenType.ESC_AND, NonTerminal.LOGIC_AND},
            {}
        });
        
        rules.put(NonTerminal.EQUALITY, new Object[][] {{NonTerminal.COMPARISON, NonTerminal.EQUALITY_P}});
        
        rules.put(NonTerminal.EQUALITY_P, new Object[][] {
            {TokenType.ESC_NOT_EQUAL, NonTerminal.EQUALITY},
            {TokenType.ESC_EQUAL_EQUAL, NonTerminal.EQUALITY},
            {}
        });
        
        rules.put(NonTerminal.COMPARISON, new Object[][] {{NonTerminal.TERM, NonTerminal.COMPARISON_P}});
        
        rules.put(NonTerminal.COMPARISON_P, new Object[][] {
            {TokenType.ESC_GREATER, NonTerminal.COMPARISON},
            {TokenType.ESC_GREATER_EQUAL, NonTerminal.COMPARISON},
            {TokenType.ESC_LESS, NonTerminal.COMPARISON},
            {TokenType.ESC_LESS_EQUAL, NonTerminal.COMPARISON},
            {}
        });
        
        rules.put(NonTerminal.TERM, new Object[][] {{NonTerminal.FACTOR, NonTerminal.TERM_P}});
        
        rules.put(NonTerminal.TERM_P, new Object[][] {
            {TokenType.ESC_MINUS, NonTerminal.TERM},
            {TokenType.ESC_PLUS, NonTerminal.TERM},
            {}
        });
        
        rules.put(NonTerminal.FACTOR, new Object[][] {
            {NonTerminal.UNARY, NonTerminal.FACTOR_P}
        });
        
        rules.put(NonTerminal.FACTOR_P, new Object[][] {
            {TokenType.ESC_SLASH, NonTerminal.FACTOR},
            {TokenType.ESC_STAR, NonTerminal.FACTOR},
            {}
        });
        
        rules.put(NonTerminal.UNARY, new Object[][] {
            {TokenType.ESC_NOT, NonTerminal.UNARY},
            {TokenType.ESC_MINUS, NonTerminal.UNARY},
            {NonTerminal.CALL}
        });
        
        rules.put(NonTerminal.CALL, new Object[][] {{NonTerminal.PRIMARY, NonTerminal.CALL_P}});
        
        rules.put(NonTerminal.CALL_P, new Object[][] {
            {TokenType.ESC_LEFT_PAREN, NonTerminal.ARGUMENTS, TokenType.ESC_RIGHT_PAREN},
            {}
        });
        
        rules.put(NonTerminal.PRIMARY, new Object[][] {
            {TokenType.ESC_TRUE},
            {TokenType.ESC_FALSE},
            {TokenType.ESC_NULL},
            {TokenType.ESC_NUMBER},
            {TokenType.ESC_STRING},
            {TokenType.ESC_IDENTIFIER},
            {TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN}
        });
        
        
        
        
        rules.put(NonTerminal.PARAMETERS, new Object[][] {
            {TokenType.ESC_IDENTIFIER, NonTerminal.PARAMETERS_P},
            {}
        });
        
        rules.put(NonTerminal.PARAMETERS_P, new Object[][] {
            {TokenType.ESC_COMMA, TokenType.ESC_IDENTIFIER, NonTerminal.PARAMETERS_P},
            {}
        });
        
        rules.put(NonTerminal.ARGUMENTS, new Object[][] {
            {NonTerminal.EXPRESSION, NonTerminal.ARGUMENTS_P},
            {}
        });
        
        rules.put(NonTerminal.ARGUMENTS_P, new Object[][] {
            {TokenType.ESC_COMMA, NonTerminal.EXPRESSION, NonTerminal.ARGUMENTS_P},
            {}
        });
    }
}
