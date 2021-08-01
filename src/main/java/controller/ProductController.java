package controller;

import authenticator.ProductResourceAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.ProductHandler;
import utils.Constants;

import java.sql.SQLException;
import java.util.logging.Logger;

public class ProductController implements BaseController {
    private final HttpServer server;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public ProductController(HttpServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        createContext(server, Constants.PRODUCT_RESOURCE_URL, new ProductHandler(), logger, new ProductResourceAuthenticator("product_resource"));
    }
}
