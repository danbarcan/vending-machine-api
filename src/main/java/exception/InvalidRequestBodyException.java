package exception;

public class InvalidRequestBodyException extends BaseException {
    public InvalidRequestBodyException() {
        super("Invalid request body!");
    }
}
