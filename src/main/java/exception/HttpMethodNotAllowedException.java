package exception;

public class HttpMethodNotAllowedException extends BaseException {
    public HttpMethodNotAllowedException() {
        super("Http method not allowed!", 405);
    }
}
