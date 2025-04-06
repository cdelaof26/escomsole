package interpreter;

/**
 *
 * @author cristopher
 */
public class LexicalScannerStatus {
    public static final int SCAN_SUCCESS = 0;
    public static final int RUN_SUCCESS = 0;
    public static final int SCAN_EOF = 99;
    
    public static final int INVALID_CHAR = -1;
    public static final int MALFORMED_NUMBER = -2;
    public static final int INVALID_STRING = -3;

    public static final int UNCLOSED_COMMENT = -4;
    
    public static boolean isError(int code) {
        return code == INVALID_CHAR || code == MALFORMED_NUMBER || code == INVALID_STRING || code == UNCLOSED_COMMENT;
    }
}
