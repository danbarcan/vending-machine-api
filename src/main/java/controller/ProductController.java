package controller;

import authenticator.ProductResourceAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.ProductHandler;
import utils.Constants;

public class ProductController {
    private final HttpServer server;

    public ProductController(HttpServer server) {
        this.server = server;
    }

    public void init() {
        server.createContext(Constants.PRODUCT_RESOURCE_URL, new ProductHandler()).setAuthenticator(new ProductResourceAuthenticator("product_resource"));
    }
}
