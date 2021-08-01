package filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import exception.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionFilter {
    public static final String REQUEST_ID_KEY = "requestId";

    public static Filter get(Logger logger) {
        return new Filter() {
            @Override
            public void doFilter(HttpExchange http, Chain chain) throws IOException {
                try {
                    chain.doFilter(http);
                } catch (BaseException e) {
                    handleException(http, e, logger, e.getStatusCode());
                } catch (Exception e) {
                    handleException(http, e, logger, 500);
                }
            }

            @Override
            public String description() {
                return "logging";
            }
        };
    }

    private static void handleException(HttpExchange http, Exception e, Logger logger, int statusCode) throws IOException {
        OutputStream output = http.getResponseBody();
        http.sendResponseHeaders(statusCode, e.getMessage().getBytes().length);

        output.write(e.getMessage().getBytes());
        output.flush();

        Object possibleRequestId = http.getAttribute(REQUEST_ID_KEY);
        String requestId = possibleRequestId instanceof String ? (String) possibleRequestId : "unknown";
        logger.info(String.format(" %s %s %s %s %s failed with message %s",
                requestId,
                http.getRequestMethod(),
                http.getRequestURI().getPath(),
                http.getRemoteAddress(),
                http.getRequestHeaders().getFirst("User-Agent"),
                e.getMessage()));

        logger.log(Level.SEVERE, "an exception was thrown", e);
    }
}
