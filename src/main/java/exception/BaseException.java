package exception;

import lombok.Getter;

import java.io.IOException;

@Getter
public class BaseException extends IOException {
    private final int statusCode;

    public BaseException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public BaseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 400;
    }

    public BaseException(String message) {
        super(message);
        this.statusCode = 400;
    }
}
