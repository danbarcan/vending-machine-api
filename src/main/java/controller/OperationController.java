package controller;

import authenticator.BuyerAuthenticator;
import com.sun.net.httpserver.HttpServer;
import handlers.BuyHandler;
import handlers.DepositHandler;
import handlers.ResetHandler;
import utils.Constants;

import java.sql.SQLException;
import java.util.logging.Logger;

public class OperationController implements BaseController {
    public static final String BUYER_REALM = "buyer_realm";

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final HttpServer server;

    public OperationController(HttpServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        createOperationContexts();
    }

    private void createOperationContexts() throws SQLException {
        createContext(server, Constants.BUY_URL, new BuyHandler(), logger, new BuyerAuthenticator(BUYER_REALM));
        createContext(server, Constants.DEPOSIT_URL, new DepositHandler(), logger, new BuyerAuthenticator(BUYER_REALM));
        createContext(server, Constants.RESET_URL, new ResetHandler(), logger, new BuyerAuthenticator(BUYER_REALM));
    }
}
