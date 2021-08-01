package exception;

public class ResourceNotUpdatedException extends BaseException {
    public ResourceNotUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotUpdatedException(String message) {
        super(message);
    }
}
