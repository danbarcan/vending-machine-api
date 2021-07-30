package controller;

import authenticator.UserResourceAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.UserHandler;
import utils.Constants;

public class UserController {
    private final HttpServer server;

    public UserController(HttpServer server) {
        this.server = server;
    }

    public void init() {
        server.createContext(Constants.USER_RESOURCE_URL, new UserHandler()).setAuthenticator(new UserResourceAuthenticator("user_resource"));
    }
}
