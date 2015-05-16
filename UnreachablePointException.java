/**
 * An exception that is thrown when a point is unreachable due to a blocked
 * path.
 */
public class UnreachablePointException extends Exception {
    public UnreachablePointException() {
        super();
    }

    public UnreachablePointException(String message) {
        super(message);
    }

    public UnreachablePointException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreachablePointException(Throwable cause) {
        super(cause);
    }
}
