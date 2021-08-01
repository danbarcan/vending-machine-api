package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import payload.BuyRequest;
import payload.BuyResponse;
import service.ProductService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class BuyHandler extends BaseHandler {
    private final ProductService productService;
    private final ObjectMapper mapper;

    public BuyHandler() throws SQLException {
        this.productService = new ProductService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.BUY_URL + "/";
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        BuyRequest buyRequest = mapper.readValue(exchange.getRequestBody(), BuyRequest.class);
        BuyResponse buyResponse = productService.buy(buyRequest, exchange.getPrincipal().getUsername());
        handleBuyResponse(exchange, exchange.getResponseBody(), mapper.writeValueAsBytes(buyResponse));

    }

    @Override
    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        long productId = parseParameterToLong(parameter);
        BuyResponse buyResponse = productService.buy(BuyRequest.builder().productIds(List.of(productId)).build(), exchange.getPrincipal().getUsername());
        handleBuyResponse(exchange, output, mapper.writeValueAsBytes(buyResponse));
    }

    private void handleBuyResponse(HttpExchange exchange, OutputStream output, byte[] bytes) throws IOException {
        byte[] buyResponseJson = bytes;
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, buyResponseJson.length);
        output.write(buyResponseJson);
        output.flush();
    }
}
