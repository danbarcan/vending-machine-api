package controller;

import authenticator.UserResourceAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.UserHandler;
import utils.Constants;

import java.sql.SQLException;
import java.util.logging.Logger;

public class UserController implements BaseController {
    private final HttpServer server;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public UserController(HttpServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        createContext(server, Constants.USER_RESOURCE_URL, new UserHandler(), logger, new UserResourceAuthenticator("user_resource"));
    }
}
