package exception;

public class ResourceNotCreatedException extends BaseException {
    public ResourceNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotCreatedException(String message) {
        super(message);
    }
}
