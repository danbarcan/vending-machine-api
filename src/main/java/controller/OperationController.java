package controller;

import authenticator.BuyerAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.BuyHandler;
import handlers.DepositHandler;
import handlers.ResetHandler;
import utils.Constants;

public class OperationController {
    public static final String BUYER_REALM = "buyer_realm";
    private final HttpServer server;

    public OperationController(HttpServer server) {
        this.server = server;
    }

    public void init() {
        server.createContext(Constants.BUY_URL, new BuyHandler()).setAuthenticator(new BuyerAuthenticator(BUYER_REALM));
        server.createContext(Constants.DEPOSIT_URL, new DepositHandler()).setAuthenticator(new BuyerAuthenticator(BUYER_REALM));
        server.createContext(Constants.RESET_URL, new ResetHandler()).setAuthenticator(new BuyerAuthenticator(BUYER_REALM));
    }
}
