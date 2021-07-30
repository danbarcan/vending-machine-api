package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.Product;
import payload.ProductRequest;
import service.ProductService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ProductHandler extends BaseHandler {
    public static final String INVALID_PARAMETER = "Invalid parameter!";
    public static final String PRODUCT_NOT_FOUND = "Product not found!";
    private final ProductService productService;
    private final ObjectMapper mapper;

    public ProductHandler() {
        this.productService = new ProductService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.PRODUCT_RESOURCE_URL + "/";
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        String respText;
        ProductRequest productRequest = mapper.readValue(exchange.getRequestBody(), ProductRequest.class);
        if (productRequest.isValid()) {
            Product product = productService.createProduct(productRequest, exchange.getPrincipal().getUsername());
            if (product != null) {
                respText = "Product added!";
                exchange.sendResponseHeaders(201, respText.getBytes().length);
            } else {
                respText = "Product not added!";
                exchange.sendResponseHeaders(400, respText.getBytes().length);
            }
        } else {
            respText = "Invalid request body!";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
        }

        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
    }

    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        List<Product> products = productService.findAll();
        if (products != null && !products.isEmpty()) {
            byte[] userJson = mapper.writeValueAsBytes(products);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, userJson.length);
            output.write(userJson);
        } else {
            String respText = "No products found!";
            exchange.sendResponseHeaders(404, respText.getBytes().length);
            output.write(respText.getBytes());
        }
    }

    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        Product product = null;
        try {
            long productId = Long.parseLong(parameter);
            product = productService.findProductById(productId);
        } catch (Exception e) {
            String respText = INVALID_PARAMETER;
            exchange.sendResponseHeaders(400, respText.getBytes().length);
            output.write(respText.getBytes());
        }

        if (product != null) {
            byte[] productJson = mapper.writeValueAsBytes(product);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, productJson.length);
            output.write(productJson);
        } else {
            String respText = PRODUCT_NOT_FOUND;
            exchange.sendResponseHeaders(404, respText.getBytes().length);
            output.write(respText.getBytes());
        }
        output.flush();
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        String respText;
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        if (parameter == null || parameter.isBlank()) {
            respText = INVALID_PARAMETER;
            exchange.sendResponseHeaders(400, respText.getBytes().length);
        } else {
            try {
                long productId = Long.parseLong(parameter);
                ProductRequest productRequest = mapper.readValue(exchange.getRequestBody(), ProductRequest.class);
                if (productRequest.isValid()) {
                    if (productService.updateProduct(productId, productRequest) > 0) {
                        respText = "Product updated successful";
                        exchange.sendResponseHeaders(200, respText.getBytes().length);
                        output.write(respText.getBytes());
                    } else {
                        respText = PRODUCT_NOT_FOUND;
                        exchange.sendResponseHeaders(404, respText.getBytes().length);
                        output.write(respText.getBytes());
                    }
                } else {
                    respText = "Invalid request body!";
                    exchange.sendResponseHeaders(400, respText.getBytes().length);
                }
            } catch (Exception e) {
                respText = PRODUCT_NOT_FOUND;
                exchange.sendResponseHeaders(404, respText.getBytes().length);
            }
        }
        output.write(respText.getBytes());
        output.flush();
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        OutputStream output = exchange.getResponseBody();
        if (parameter == null || parameter.isBlank()) {
            String respText = INVALID_PARAMETER;
            exchange.sendResponseHeaders(400, respText.getBytes().length);
            output.write(respText.getBytes());
        } else {
            try {
                long productId = Long.parseLong(parameter);
                if (productService.deleteProductById(productId) >= 0) {
                    String respText = "Product deleted successfully";
                    exchange.sendResponseHeaders(200, respText.getBytes().length);
                    output.write(respText.getBytes());
                } else {
                    String respText = PRODUCT_NOT_FOUND;
                    exchange.sendResponseHeaders(404, respText.getBytes().length);
                    output.write(respText.getBytes());
                }
            } catch (Exception e) {
                String respText = PRODUCT_NOT_FOUND;
                exchange.sendResponseHeaders(404, respText.getBytes().length);
                output.write(respText.getBytes());
            }
        }
        output.flush();
    }
}
