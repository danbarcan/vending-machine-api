package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidParameterException;
import exception.ResourceNotDeletedException;
import exception.ResourceNotUpdatedException;
import model.Product;
import payload.ProductRequest;
import service.ProductService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class ProductHandler extends BaseHandler {
    private final ProductService productService;
    private final ObjectMapper mapper;

    public ProductHandler() throws SQLException {
        this.productService = new ProductService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.PRODUCT_RESOURCE_URL + "/";
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        String respText = "";
        ProductRequest productRequest = mapper.readValue(exchange.getRequestBody(), ProductRequest.class);
        if (productRequest.isValid()) {
            Product product = productService.createProduct(productRequest, exchange.getPrincipal().getUsername());
            if (product != null) {
                respText = "Product added!";
                exchange.sendResponseHeaders(201, respText.getBytes().length);
            }
        }

        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
    }

    @Override
    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        byte[] userJson = mapper.writeValueAsBytes(productService.findAll());
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, userJson.length);
        output.write(userJson);
    }

    @Override
    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        long productId = parseParameterToLong(parameter);
        Product product = productService.findProductById(productId);

        byte[] productJson = mapper.writeValueAsBytes(product);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, productJson.length);
        output.write(productJson);

        output.flush();
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        if (parameter == null || parameter.isBlank()) {
            throw new InvalidParameterException();
        } else {
            long productId = Long.parseLong(parameter);
            ProductRequest productRequest = mapper.readValue(exchange.getRequestBody(), ProductRequest.class);
            if (productRequest.isValid()) {
                if (productService.updateProduct(productId, productRequest) > 0) {
                    String respText = "Product updated successful";
                    exchange.sendResponseHeaders(200, respText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } else {
                    throw new ResourceNotUpdatedException("Product was not updated!");
                }
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        if (parameter == null || parameter.isBlank()) {
            throw new InvalidParameterException();
        } else {
            long productId = parseParameterToLong(parameter);
            if (productService.deleteProductById(productId) >= 0) {
                String respText = "Product deleted successfully";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } else {
                throw new ResourceNotDeletedException("Product was not deleted");
            }
        }
    }
}
