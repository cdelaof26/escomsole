package interpreter;

/**
 *
 * @author cristopher
 */
public class Status {
    public static final int SCAN_SUCCESS = 0;
    public static final int RUN_SUCCESS = 0;
    public static final int FILE_NOT_FOUND = 1;
    
    public static final int SYNTAX_ERROR = -1;
    public static final int MALFORMED_NUMBER = -2;
    public static final int STRING_CHARTER_INVALID = -3;
    
    public static final int INVALID_ESCOMSOLE_CALL = -1;


    
    public static boolean isError(int code) {
        return code == SYNTAX_ERROR || code == MALFORMED_NUMBER || code == STRING_CHARTER_INVALID;
    }
}
