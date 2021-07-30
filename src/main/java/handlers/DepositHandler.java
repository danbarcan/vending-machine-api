package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import payload.DepositRequest;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class DepositHandler extends BaseHandler {
    private final UserService userService;
    private final ObjectMapper mapper;

    public DepositHandler() {
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.DEPOSIT_URL + "/";
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        String respText;
        Map<Integer, Long> coins = mapper.readValue(exchange.getRequestBody(), DepositRequest.class).toMap();

        if (userService.addCoinsToUserDeposit(exchange.getPrincipal().getUsername(), sumOfCoins(coins)) >= 0) {
            respText = "User deposit successfully";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
        } else {
            respText = "User not found";
            exchange.sendResponseHeaders(404, respText.getBytes().length);
        }

        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
    }

    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
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

    private long sumOfCoins(Map<Integer, Long> coins) {
        return coins.entrySet().stream().map(entry -> entry.getKey() * entry.getValue()).reduce(0l, Long::sum);
    }
}
