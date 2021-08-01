package exception;

public class ResourceNotDeletedException extends BaseException {
    public ResourceNotDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotDeletedException(String message) {
        super(message);
    }
}
