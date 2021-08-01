package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class ResetHandler extends BaseHandler {
    private final UserService userService;

    public ResetHandler() throws SQLException {
        this.userService = new UserService();
        this.resourceUrl = Constants.RESET_URL + "/";
    }

    @Override
    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        String respText;
        String username = exchange.getPrincipal().getUsername();
        if (userService.resetUserDeposit(username) >= 0) {
            respText = "User deposit reset successful";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
        } else {
            respText = "User not found!";
            exchange.sendResponseHeaders(404, respText.getBytes().length);
        }
        output.write(respText.getBytes());
        output.flush();
    }
}
