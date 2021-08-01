package exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, 404);
    }

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
