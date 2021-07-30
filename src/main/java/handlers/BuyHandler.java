package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import payload.BuyRequest;
import payload.BuyResponse;
import service.ProductService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class BuyHandler extends BaseHandler {
    private final ProductService productService;
    private final ObjectMapper mapper;

    public BuyHandler() {
        this.productService = new ProductService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.BUY_URL + "/";
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        BuyRequest buyRequest = mapper.readValue(exchange.getRequestBody(), BuyRequest.class);
        BuyResponse buyResponse = productService.buy(buyRequest, exchange.getPrincipal().getUsername());
        handleBuyResponse(exchange, output, buyResponse != null, mapper.writeValueAsBytes(buyResponse), "Purchase cannot be completed!");
        output.flush();
    }

    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }

    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        try {
            long productId = Long.parseLong(parameter);
            BuyResponse buyResponse = productService.buy(BuyRequest.builder().productIds(List.of(productId)).build(), exchange.getPrincipal().getUsername());
            handleBuyResponse(exchange, output, buyResponse != null, mapper.writeValueAsBytes(buyResponse), "Purchase cannot be completed!");
        } catch (Exception e) {
            String respText = "Invalid parameter!";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
            output.write(respText.getBytes());
        }

        output.flush();
    }

    private void handleBuyResponse(HttpExchange exchange, OutputStream output, boolean b, byte[] bytes, String s) throws IOException {
        if (b) {
            byte[] buyResponseJson = bytes;
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, buyResponseJson.length);
            output.write(buyResponseJson);
        } else {
            exchange.sendResponseHeaders(404, s.getBytes().length);
            output.write(s.getBytes());
        }
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
    }
}
