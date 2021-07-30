package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;

public class ResetHandler extends BaseHandler {
    private final UserService userService;

    public ResetHandler() {
        this.userService = new UserService();
        this.resourceUrl = Constants.RESET_URL + "/";
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }

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

    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }
}
