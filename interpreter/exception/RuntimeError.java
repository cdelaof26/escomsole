package interpreter.exception;

/**
 *
 * @author cristopher
 */
public class RuntimeError extends RuntimeException {
    public RuntimeError(Object name, String cause) {
        super(name.toString() + " " + cause);
    }
}
