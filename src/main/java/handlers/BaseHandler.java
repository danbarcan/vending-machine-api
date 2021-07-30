package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHandler implements HttpHandler {
    protected String resourceUrl;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (urlIsCorrect(exchange)) {
            if (Constants.HTTP_POST.equals(exchange.getRequestMethod())) {
                handlePost(exchange);
            } else if (Constants.HTTP_GET.equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else if (Constants.HTTP_PUT.equals(exchange.getRequestMethod())) {
                handlePut(exchange);
            } else if (Constants.HTTP_DELETE.equals(exchange.getRequestMethod())) {
                handleDelete(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
        exchange.close();
    }

    protected abstract void handlePost(HttpExchange exchange) throws IOException;

    protected void handleGet(HttpExchange exchange) throws IOException {
        String parameter = extractExtraParams(exchange.getRequestURI().toString());
        OutputStream output = exchange.getResponseBody();
        if (parameter == null || parameter.isBlank()) {
            handleGetAll(exchange, output);
        } else {
            handleGetOneByParameter(exchange, output, parameter);
        }
        output.flush();
    }

    protected abstract void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException;

    protected abstract void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException;

    protected abstract void handlePut(HttpExchange exchange) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange) throws IOException;

    private boolean urlIsCorrect(HttpExchange exchange) {
        String extraParams = extractExtraParams(exchange.getRequestURI().toString());
        switch (exchange.getRequestMethod()) {
            case Constants.HTTP_GET:
                return extraParams == null || extraParams.length() == 0 || (extraParams.length() == 1 && extraParams.equals("/")) || isNumber(extraParams) || isValidUsername(extraParams);
            case Constants.HTTP_POST:
                return extraParams == null || extraParams.length() == 0 || (extraParams.length() == 1 && extraParams.equals("/"));
            case Constants.HTTP_PUT:
            case Constants.HTTP_DELETE:
                return isNumber(extraParams);
            default:
                return false;
        }
    }

    private String extractExtraParams(String url) {
        return url.endsWith("/") || Character.isDigit(url.charAt(url.length() - 1)) ? url.replaceAll(resourceUrl, "") : url.replaceAll(resourceUrl.substring(0, resourceUrl.length() - 1), "");
    }

    private boolean isNumber(String string) {
        try {
            Long.parseLong(string.replace("/", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidUsername(String string) {
        return !string.contains("/");
    }
}
