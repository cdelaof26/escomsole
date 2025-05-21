// Source code is decompiled from a .class file using FernFlower decompiler.
package modeling;

import java.util.HashMap;

public class GramaticalRules {
   public static HashMap<NoTerminales, Object[][]> Rules = new HashMap();

   public GramaticalRules() {
   }

   static {
      Rules.put(NoTerminales.PROGRAM, new Object[][]{{NoTerminales.DECLARATION}});
      Rules.put(NoTerminales.DECLARATION, new Object[][]{{NoTerminales.FUN_DECL, NoTerminales.DECLARATION}, {NoTerminales.VAR_DECL, NoTerminales.DECLARATION}, {NoTerminales.STATEMENT, NoTerminales.DECLARATION}, {null}});
      Rules.put(NoTerminales.FUN_DECL, new Object[][]{{TokenType.ESC_FUN, TokenType.ESC_IDENTIFIER, TokenType.ESC_LEFT_PAREN, NoTerminales.PARAMETERS, TokenType.ESC_RIGHT_PAREN, NoTerminales.BLOCK}});
      Rules.put(NoTerminales.VAR_DECL, new Object[][]{{TokenType.ESC_VAR, TokenType.ESC_IDENTIFIER, NoTerminales.VAR_INIT, TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.VAR_INIT, new Object[][]{{TokenType.ESC_EQUAL, NoTerminales.EXPRESSION}, {null}});
      Rules.put(NoTerminales.STATEMENT, new Object[][]{{NoTerminales.EXPR_STMT}, {NoTerminales.FOR_STMT}, {NoTerminales.IF_STMT}, {NoTerminales.PRINT_STMT}, {NoTerminales.RETURN_STMT}, {NoTerminales.WHILE_STMT}, {NoTerminales.BLOCK}});
      Rules.put(NoTerminales.EXPR_STMT, new Object[][]{{NoTerminales.EXPRESSION, TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.FOR_STMT, new Object[][]{{TokenType.ESC_FOR, TokenType.ESC_LEFT_PAREN, NoTerminales.FOR_STMT_INIT, NoTerminales.FOR_STMT_COND, NoTerminales.FOR_STMT_INC, TokenType.ESC_RIGHT_PAREN, NoTerminales.STATEMENT}});
      Rules.put(NoTerminales.FOR_STMT_INIT, new Object[][]{{NoTerminales.VAR_DECL}, {NoTerminales.EXPR_STMT}, {TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.FOR_STMT_COND, new Object[][]{{NoTerminales.EXPRESSION}, {TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.FOR_STMT_INC, new Object[][]{{NoTerminales.EXPRESSION}, {null}});
      Rules.put(NoTerminales.IF_STMT, new Object[][]{{TokenType.ESC_IF, TokenType.ESC_LEFT_PAREN, NoTerminales.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NoTerminales.STATEMENT, NoTerminales.ELSE_STATEMENT}});
      Rules.put(NoTerminales.ELSE_STATEMENT, new Object[][]{{TokenType.ESC_ELSE, NoTerminales.STATEMENT}, {null}});
      Rules.put(NoTerminales.PRINT_STMT, new Object[][]{{TokenType.ESC_PRINT, NoTerminales.EXPRESSION, TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.RETURN_STMT, new Object[][]{{TokenType.ESC_RETURN, NoTerminales.RETURN_EXP_OPC, TokenType.ESC_SEMICOLON}});
      Rules.put(NoTerminales.RETURN_EXP_OPC, new Object[][]{{NoTerminales.EXPRESSION}, {null}});
      Rules.put(NoTerminales.WHILE_STMT, new Object[][]{{TokenType.ESC_WHILE, TokenType.ESC_LEFT_PAREN, NoTerminales.EXPRESSION, TokenType.ESC_RIGHT_PAREN, NoTerminales.STATEMENT}});
      Rules.put(NoTerminales.BLOCK, new Object[][]{{TokenType.ESC_LEFT_BRACE, NoTerminales.DECLARATION, TokenType.ESC_RIGHT_BRACE}});
      Rules.put(NoTerminales.EXPRESSION, new Object[][]{{NoTerminales.ASSIGNMENT}});
      Rules.put(NoTerminales.ASSIGNMENT, new Object[][]{{NoTerminales.LOGIC_OR, NoTerminales.ASSIGNMENT_OPC}});
      Rules.put(NoTerminales.ASSIGNMENT_OPC, new Object[][]{{TokenType.ESC_EQUAL, NoTerminales.EXPRESSION}, {null}});
      Rules.put(NoTerminales.LOGIC_OR, new Object[][]{{NoTerminales.LOGIC_AND, NoTerminales.LOGIC_OR_P}});
      Rules.put(NoTerminales.LOGIC_OR_P, new Object[][]{{TokenType.ESC_OR, NoTerminales.LOGIC_OR}, {null}});
      Rules.put(NoTerminales.LOGIC_AND, new Object[][]{{NoTerminales.EQUALITY, NoTerminales.LOGIC_AND_P}});
      Rules.put(NoTerminales.LOGIC_AND_P, new Object[][]{{TokenType.ESC_AND, NoTerminales.LOGIC_AND}, {null}});
      Rules.put(NoTerminales.EQUALITY, new Object[][]{{NoTerminales.COMPARISON, NoTerminales.EQUALITY_P}});
      Rules.put(NoTerminales.EQUALITY_P, new Object[][]{{TokenType.ESC_NOT_EQUAL, NoTerminales.EQUALITY}, {TokenType.ESC_EQUAL_EQUAL, NoTerminales.EQUALITY}, {null}});
      Rules.put(NoTerminales.COMPARISON, new Object[][]{{NoTerminales.TERM, NoTerminales.COMPARISON_P}});
      Rules.put(NoTerminales.COMPARISON_P, new Object[][]{{TokenType.ESC_GREATER, NoTerminales.COMPARISON}, {TokenType.ESC_GREATER_EQUAL, NoTerminales.COMPARISON}, {TokenType.ESC_LESS, NoTerminales.COMPARISON}, {TokenType.ESC_LESS_EQUAL, NoTerminales.COMPARISON}, {null}});
      Rules.put(NoTerminales.TERM, new Object[][]{{NoTerminales.FACTOR, NoTerminales.TERM_P}});
      Rules.put(NoTerminales.TERM_P, new Object[][]{{TokenType.ESC_MINUS, NoTerminales.TERM}, {TokenType.ESC_PLUS, NoTerminales.TERM}, {null}});
      Rules.put(NoTerminales.FACTOR, new Object[][]{{NoTerminales.UNARY, NoTerminales.FACTOR_P}});
      Rules.put(NoTerminales.FACTOR_P, new Object[][]{{TokenType.ESC_SLASH, NoTerminales.FACTOR}, {TokenType.ESC_STAR, NoTerminales.FACTOR}, {null}});
      Rules.put(NoTerminales.UNARY, new Object[][]{{TokenType.ESC_NOT, NoTerminales.UNARY}, {TokenType.ESC_MINUS, NoTerminales.UNARY}, {NoTerminales.CALL}});
      Rules.put(NoTerminales.CALL, new Object[][]{{NoTerminales.PRIMARY, NoTerminales.CALL_P}});
      Rules.put(NoTerminales.CALL_P, new Object[][]{{TokenType.ESC_LEFT_PAREN, NoTerminales.ARGUMENTS, TokenType.ESC_RIGHT_PAREN}, {null}});
      Rules.put(NoTerminales.PRIMARY, new Object[][]{{TokenType.ESC_TRUE}, {TokenType.ESC_FALSE}, {TokenType.ESC_NULL}, {TokenType.ESC_NUMBER}, {TokenType.ESC_STRING}, {TokenType.ESC_IDENTIFIER}, {TokenType.ESC_LEFT_PAREN, NoTerminales.EXPRESSION, TokenType.ESC_RIGHT_PAREN}});
      Rules.put(NoTerminales.PARAMETERS, new Object[][]{{TokenType.ESC_IDENTIFIER, NoTerminales.PARAMETERS_P}, {null}});
      Rules.put(NoTerminales.PARAMETERS_P, new Object[][]{{TokenType.ESC_COMMA, TokenType.ESC_IDENTIFIER, NoTerminales.PARAMETERS_P}, {null}});
      Rules.put(NoTerminales.ARGUMENTS, new Object[][]{{NoTerminales.EXPRESSION, NoTerminales.ARGUMENTS_P}, {null}});
      Rules.put(NoTerminales.ARGUMENTS_P, new Object[][]{{TokenType.ESC_COMMA, NoTerminales.EXPRESSION, NoTerminales.ARGUMENTS_P}, {null}});
   }
}
