package rest.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import model.Product;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static rest.tests.TestUtils.*;

public class ProductTests {
    private static String username;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createProduct_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Response response = createProduct(jsonAsMap, user.getUsername(), null);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Product added!", response.getBody().prettyPrint());
    }

    @Test
    public void createProduct_buyer_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Response response = createProduct(jsonAsMap, user.getUsername(), null);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void createProduct_wrongPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Response response = createProduct(jsonAsMap, user.getUsername(), RANDOM);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void createProduct_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = productPayload(username, 10, 10);
        Response response = createProductWithoutAuth(jsonAsMap);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void createProduct_badParameters_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayloadIncomplete(username, 10);
        Response response = createProduct(jsonAsMap, user.getUsername(), null);

        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Invalid request body!", response.getBody().prettyPrint());
    }

    @Test
    public void readAllProducts_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        createProduct(jsonAsMap, user.getUsername(), PASSWORD);

        Response response = readAllProducts();
        List<Product> products = getProductsFromResponse(objectMapper, response);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(products.size() > 0);
    }

    @Test
    public void readProduct_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        createProduct(jsonAsMap, user.getUsername(), PASSWORD);

        Product product = getProductByName(objectMapper, username);

        Response response = readProduct(product.getId());
        Product receivedProduct = objectMapper.readValue(response.getBody().prettyPrint(), Product.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(receivedProduct, product);
    }

    @Test
    public void readProduct_unavailable_fails() throws JsonProcessingException {
        Response response = readProduct(Long.MAX_VALUE);

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals("Product not found!", response.getBody().prettyPrint());
    }

    @Test
    public void updateProduct_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        Response response = updateProduct(jsonAsMap, user, null, product.getId());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Product updated successful", response.getBody().prettyPrint());
    }

    @Test
    public void updateProduct_buyer_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        String newUsername = "test" + new Random().nextInt(1000000000);
        jsonAsMap = userPayload(newUsername, PASSWORD, BUYER);
        user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 100, 10);

        Response response = updateProduct(jsonAsMap, user, null, product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void updateProduct_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        jsonAsMap = productPayload(username, 100, 10);
        Response response = updateProductWithoutAuth(jsonAsMap, product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void updateProduct_wrongPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        jsonAsMap = productPayload(username, 100, 10);

        Response response = updateProduct(jsonAsMap, user, RANDOM, product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteProduct_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        Response response = deleteProduct(user, null, product.getId());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Product deleted successfully", response.getBody().prettyPrint());
    }

    @Test
    public void deleteProduct_buyer_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        String newUsername = "test" + new Random().nextInt(1000000000);
        jsonAsMap = userPayload(newUsername, PASSWORD, BUYER);
        user = createAndGetUser(objectMapper, jsonAsMap);

        Response response = deleteProduct(user, null, product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteProduct_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        Response response = deleteProductWithoutAuth(product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteProduct_wrongPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User user = createAndGetUser(objectMapper, jsonAsMap);

        jsonAsMap = productPayload(username, 10, 10);
        Product product = createAndGetProduct(jsonAsMap, user);

        Response response = deleteProduct(user, RANDOM, product.getId());

        Assertions.assertEquals(401, response.statusCode());
    }

    private Product createAndGetProduct(Map<String, Object> jsonAsMap, User user) throws JsonProcessingException {
        createProduct(jsonAsMap, user.getUsername(), PASSWORD);

        Response response = readAllProducts();
        List<Product> products = getProductsFromResponse(objectMapper, response);
        return products.stream().filter(product -> jsonAsMap.get("productName").toString().equals(product.getProductName())).findFirst().get();
    }
}