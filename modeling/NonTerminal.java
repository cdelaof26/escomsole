package modeling;

/**
 *
 * @author cristopher
 */
public enum NonTerminal {
    PROGRAM, 
    
    // Declaraciones
    DECLARATION, FUN_DECL, VAR_DECL, VAR_INIT, 
    
    // Sentencias
    STATEMENT, EXPR_STMT, FOR_STMT, FOR_STMT_INIT, FOR_STMT_COND, FOR_STMT_INC, 
    IF_STMT, ELSE_STATEMENT, PRINT_STMT, RETURN_STMT, RETURN_EXP_OPC, WHILE_STMT, 
    BLOCK,
    
    // Expresiones
    EXPRESSION, ASSIGNMENT, ASSIGNMENT_OPC, LOGIC_OR, LOGIC_OR_P, 
    LOGIC_AND, LOGIC_AND_P, EQUALITY, EQUALITY_P, COMPARISON, COMPARISON_P, 
    TERM, TERM_P, FACTOR, FACTOR_P, UNARY, CALL, CALL_P, PRIMARY, 
    
    // Added
    ARRAY,
    
    // Otras
    PARAMETERS, PARAMETERS_P, ARGUMENTS, ARGUMENTS_P
}
