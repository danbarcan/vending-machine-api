package filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Logger;

public class LoggingFilter {
    public static final String REQUEST_ID_KEY = "requestId";

    public static Filter get(Logger logger) {
        return new Filter() {
            @Override
            public void doFilter(HttpExchange http, Chain chain) throws IOException {
                Object possibleRequestId = http.getAttribute(REQUEST_ID_KEY);
                String requestId = possibleRequestId instanceof String ? (String) possibleRequestId : "unknown";
                logger.info(String.format("%s %s %s %s %s",
                        requestId,
                        http.getRequestMethod(),
                        http.getRequestURI().getPath(),
                        http.getRemoteAddress(),
                        http.getRequestHeaders().getFirst("User-Agent")));
                chain.doFilter(http);
            }

            @Override
            public String description() {
                return "logging";
            }
        };
    }
}
