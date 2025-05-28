package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import modeling.NonTerminal;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class SyntacticGrammar {
    public static final HashMap<NonTerminal, Object[][]> rules = new HashMap<>();
    
    static {
        
        /**
         * Testing grammar 1
         * E -> TE'
         * E' -> +TE'|eps
         * T -> FT'
         * T' -> *FT'|eps
         * F -> (E)|id
         * 
         * NonTerminal: E, T, E_P, F, T_P,
         *
        rules.put(NonTerminal.E, new Object[][]{{NonTerminal.T, NonTerminal.E_P}});
        rules.put(NonTerminal.E_P, new Object[][]{{TokenType.ESC_PLUS, NonTerminal.T, NonTerminal.E_P}, null});
        rules.put(NonTerminal.T, new Object[][]{{NonTerminal.F, NonTerminal.T_P}});
        rules.put(NonTerminal.T_P, new Object[][]{{TokenType.ESC_STAR, NonTerminal.F, NonTerminal.T_P}, null});
        rules.put(NonTerminal.F, new Object[][]{{TokenType.ESC_LEFT_PAREN, NonTerminal.E, TokenType.ESC_RIGHT_PAREN}, {TokenType.ESC_IDENTIFIER}});
        // */
        
        /**
         * Testing grammar 2
         * A -> Aa|BCD
         * B -> b|eps
         * C -> c|eps
         * D -> d|Ce
         * 
         * NonTerminal: A, B, C, D,
         * TokenType: a, b, c, d, e,
         *
        rules.put(NonTerminal.A, new Object[][]{{NonTerminal.A, TokenType.a}, {NonTerminal.B, NonTerminal.C, NonTerminal.D}});
        rules.put(NonTerminal.B, new Object[][]{{TokenType.b}, null});
        rules.put(NonTerminal.C, new Object[][]{{TokenType.c}, null});
        rules.put(NonTerminal.D, new Object[][]{{TokenType.d}, {NonTerminal.C, TokenType.e}});
        // */
        
        
        rules.put(NonTerminal.PROGRAM, new Object[][]{
            {NonTerminal.DECLARATION}
        });
        
        
        
        
        rules.put(NonTerminal.DECLARATION, new Object[][]{
            {NonTerminal.FUN_DECL, NonTerminal.DECLARATION},
            {NonTerminal.VAR_DECL, NonTerminal.DECLARATION},
            {NonTerminal.STATEMENT, NonTerminal.DECLARATION},
            null
        });
        
        rules.put(NonTerminal.FUN_DECL, new Object[][]{
            {TokenType.ESC_FUN, TokenType.ESC_IDENTIFIER, TokenType.ESC_LEFT_PAREN, NonTerminal.PARAMETERS, TokenType.ESC_RIGHT_PAREN, NonTerminal.BLOCK}
        });
        
        rules.put(NonTerminal.VAR_DECL, new Object[][]{
            {TokenType.ESC_VAR, TokenType.ESC_IDENTIFIER, NonTerminal.VAR_INIT, TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.VAR_INIT, new Object[][]{
            {TokenType.ESC_EQUAL, NonTerminal.EXPRESSION}, 
            null
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
            null
        });
        
        rules.put(NonTerminal.IF_STMT, new Object[][] {
            {TokenType.ESC_IF, TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NonTerminal.STATEMENT, NonTerminal.ELSE_STATEMENT}
        });
        
        rules.put(NonTerminal.ELSE_STATEMENT, new Object[][] {
            {TokenType.ESC_ELSE, NonTerminal.STATEMENT}, 
            null
        });
        
        rules.put(NonTerminal.PRINT_STMT, new Object[][] {
            {TokenType.ESC_PRINT, NonTerminal.EXPRESSION, TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.RETURN_STMT, new Object[][] {
            {TokenType.ESC_RETURN, NonTerminal.RETURN_EXP_OPC, TokenType.ESC_SEMICOLON}
        });
        
        rules.put(NonTerminal.RETURN_EXP_OPC, new Object[][] {
            {NonTerminal.EXPRESSION}, 
            null
        });
        
        rules.put(NonTerminal.WHILE_STMT, new Object[][] {
            {TokenType.ESC_WHILE, TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NonTerminal.STATEMENT}
        });
        
        rules.put(NonTerminal.BLOCK, new Object[][] {
            {TokenType.ESC_LEFT_BRACE, NonTerminal.DECLARATION, TokenType.ESC_RIGHT_BRACE}
        });
        
        
        
        
        rules.put(NonTerminal.EXPRESSION, new Object[][] {
            {NonTerminal.ASSIGNMENT}
        });
        
        rules.put(NonTerminal.ASSIGNMENT, new Object[][] {
            {NonTerminal.CONDITIONAL, NonTerminal.ASSIGNMENT_OPC}
        });
        
        rules.put(NonTerminal.ASSIGNMENT_OPC, new Object[][] {
            {TokenType.ESC_EQUAL, NonTerminal.EXPRESSION},
            
            // Added feature, +=, -=, *= and /= operators
            {TokenType.ESC_STAR_EQUAL, NonTerminal.EXPRESSION}, 
            {TokenType.ESC_SLASH_EQUAL, NonTerminal.EXPRESSION}, 
            {TokenType.ESC_PLUS_EQUAL, NonTerminal.EXPRESSION}, 
            {TokenType.ESC_MINUS_EQUAL, NonTerminal.EXPRESSION}, 
            
            null
        });
        
        rules.put(NonTerminal.CONDITIONAL, new Object[][] {
            {NonTerminal.LOGIC_OR, NonTerminal.CONDITIONAL_P}
        });
        
        rules.put(NonTerminal.CONDITIONAL_P, new Object[][] {
            {TokenType.ESC_QUESTION_MARK, NonTerminal.CONDITIONAL, TokenType.ESC_COLON, NonTerminal.CONDITIONAL},
            null
        });
        
        rules.put(NonTerminal.LOGIC_OR, new Object[][] {
            {NonTerminal.LOGIC_AND, NonTerminal.LOGIC_OR_P}
        });
        
        rules.put(NonTerminal.LOGIC_OR_P, new Object[][] {
            {TokenType.ESC_OR, NonTerminal.LOGIC_OR},
            null
        });
        
        rules.put(NonTerminal.LOGIC_AND, new Object[][] {
            {NonTerminal.EQUALITY, NonTerminal.LOGIC_AND_P}
        });
        
        rules.put(NonTerminal.LOGIC_AND_P, new Object[][] {
            {TokenType.ESC_AND, NonTerminal.LOGIC_AND},
            null
        });
        
        rules.put(NonTerminal.EQUALITY, new Object[][] {
            {NonTerminal.COMPARISON, NonTerminal.EQUALITY_P}
        });
        
        rules.put(NonTerminal.EQUALITY_P, new Object[][] {
            {TokenType.ESC_NOT_EQUAL, NonTerminal.EQUALITY},
            {TokenType.ESC_EQUAL_EQUAL, NonTerminal.EQUALITY},
            null
        });
        
        rules.put(NonTerminal.COMPARISON, new Object[][] {
            {NonTerminal.TERM, NonTerminal.COMPARISON_P}
        });
        
        rules.put(NonTerminal.COMPARISON_P, new Object[][] {
            {TokenType.ESC_GREATER, NonTerminal.COMPARISON},
            {TokenType.ESC_GREATER_EQUAL, NonTerminal.COMPARISON},
            {TokenType.ESC_LESS, NonTerminal.COMPARISON},
            {TokenType.ESC_LESS_EQUAL, NonTerminal.COMPARISON},
            null
        });
        
        rules.put(NonTerminal.TERM, new Object[][] {
            {NonTerminal.FACTOR, NonTerminal.TERM_P}
        });
        
        rules.put(NonTerminal.TERM_P, new Object[][] {
            {TokenType.ESC_MINUS, NonTerminal.TERM},
            {TokenType.ESC_PLUS, NonTerminal.TERM},
            
            // Added feature: ++ and -- operators
            {TokenType.ESC_MINUS_MINUS},
            {TokenType.ESC_PLUS_PLUS},

            null
        });
        
        rules.put(NonTerminal.FACTOR, new Object[][] {
            {NonTerminal.UNARY, NonTerminal.FACTOR_P}
        });
        
        rules.put(NonTerminal.FACTOR_P, new Object[][] {
            {TokenType.ESC_SLASH, NonTerminal.FACTOR},
            {TokenType.ESC_STAR, NonTerminal.FACTOR},
            null
        });
        
        rules.put(NonTerminal.UNARY, new Object[][] {
            {TokenType.ESC_NOT, NonTerminal.UNARY},
            {TokenType.ESC_MINUS, NonTerminal.UNARY},
            {NonTerminal.CALL}
        });
        
        rules.put(NonTerminal.CALL, new Object[][] {
            {NonTerminal.PRIMARY, NonTerminal.CALL_P}
        });
        
        rules.put(NonTerminal.CALL_P, new Object[][] {
            {TokenType.ESC_LEFT_PAREN, NonTerminal.ARGUMENTS, TokenType.ESC_RIGHT_PAREN},
            
            // Added feature, support for array access notation: "a[v]"
            {TokenType.ESC_LEFT_BRACKET, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_BRACKET},
            
            // Added feature, ternary operator: EXPRESSION ? EXPRESSION : EXPRESSION
            // The first expression is already captured
            {TokenType.ESC_QUESTION_MARK, NonTerminal.EXPRESSION, TokenType.ESC_COLON, NonTerminal.EXPRESSION},
            
            null
        });
        
        // Added feature, support for array notation: "[]", "[v]", "[v, v, ···, v]"
        // ARRAY -> [ARGUMENTS]
        rules.put(NonTerminal.ARRAY, new Object[][] {
            {TokenType.ESC_LEFT_BRACKET, NonTerminal.ARGUMENTS, TokenType.ESC_RIGHT_BRACKET}
        });
        
        rules.put(NonTerminal.PRIMARY, new Object[][] {
            {TokenType.ESC_TRUE},
            {TokenType.ESC_FALSE},
            {TokenType.ESC_NULL},
            {TokenType.ESC_NUMBER},
            {TokenType.ESC_FLOATING_NUMBER},
            {TokenType.ESC_DOUBLE_NUMBER},
            {TokenType.ESC_STRING},
            {TokenType.ESC_IDENTIFIER},
            {TokenType.ESC_LEFT_PAREN, NonTerminal.EXPRESSION, TokenType.ESC_RIGHT_PAREN},
            
            // Added feature, support for array initialization: "var a = [1, 2, 3];"
            {NonTerminal.ARRAY}
        });
        
        
        
        
        rules.put(NonTerminal.PARAMETERS, new Object[][] {
            {TokenType.ESC_IDENTIFIER, NonTerminal.PARAMETERS_P},
            null
        });
        
        rules.put(NonTerminal.PARAMETERS_P, new Object[][] {
            {TokenType.ESC_COMMA, TokenType.ESC_IDENTIFIER, NonTerminal.PARAMETERS_P},
            null
        });
        
        rules.put(NonTerminal.ARGUMENTS, new Object[][] {
            {NonTerminal.EXPRESSION, NonTerminal.ARGUMENTS_P},
            null
        });
        
        rules.put(NonTerminal.ARGUMENTS_P, new Object[][] {
            {TokenType.ESC_COMMA, NonTerminal.EXPRESSION, NonTerminal.ARGUMENTS_P},
            null
        });
        
        // */
    }

    /**
     * This function calculates the first set for a value.<br>
     * Pseudo code extracted from<br>
     * https://campusvirtual.ull.es/ocw/pluginfile.php/3850/mod_resource/content/0/perlexamples/node194.html<br>
     * 2012-05-22
     * 
     * @param alpha the NonTerminal or TokenType (terminal) value to calculate 
     * the first set from
     * @return the first set of the provided value
     */
    public static ArrayList<Object> first(Object alpha) {
//        System.out.println("alpha = " + alpha);
//        System.out.println("prev = " + prev);

        ArrayList<Object> set = new ArrayList<>();
        
        if (alpha instanceof TokenType) {
            set.add(alpha);
            return set;
        }
        
        Object [][] productions = rules.get((NonTerminal) alpha);
        for (Object [] p : productions) {
            if (p == null) {
                if (!set.contains(p))
                    set.add(p);

                continue;
            }

            boolean containsEpsilon = false;
            for (Object pe : p) {
                if (pe.equals(alpha))
                    continue;
                
                for (Object o : first(pe)) {
                    if (o == null) {
                        containsEpsilon = true;
                        continue;
                    }

                    if (!set.contains(o))
                        set.add(o);
                }

                if (!containsEpsilon)
                    break;
            }

//                if (!containsEpsilon)
//                    break;
        }

        if (!set.contains(null) && productions[productions.length - 1] == null)
            set.add(null);
        
        return set;
    }
}
