package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import payload.DepositRequest;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class DepositHandler extends BaseHandler {
    private final UserService userService;
    private final ObjectMapper mapper;

    public DepositHandler() throws SQLException {
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.DEPOSIT_URL + "/";
    }

    @Override
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

    private long sumOfCoins(Map<Integer, Long> coins) {
        return coins.entrySet().stream().map(entry -> entry.getKey() * entry.getValue()).reduce(0l, Long::sum);
    }
}
