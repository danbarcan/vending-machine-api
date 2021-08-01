package controller;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import filter.ExceptionFilter;
import filter.LoggingFilter;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public interface BaseController {

    void init() throws SQLException;

    default void createContext(HttpServer server, String path, HttpHandler handler, Logger logger, BasicAuthenticator authenticator) {
        HttpContext context = server.createContext(path, handler);
        context.getFilters().addAll(List.of(LoggingFilter.get(logger), ExceptionFilter.get(logger)));
        context.setAuthenticator(authenticator);
    }
}
