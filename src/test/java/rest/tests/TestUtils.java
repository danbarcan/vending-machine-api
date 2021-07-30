package rest.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Product;
import model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TestUtils {
    private static final int PORT = 8000;
    private static final String BASE_URL = "http://localhost/api";
    public static final String PASSWORD = "test12";
    public static final String BUYER = "BUYER";
    public static final String SELLER = "SELLER";
    public static final String RANDOM = "random";

    public static Map<String, Object> userPayload(String username, String password, String role) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("username", username);
        jsonAsMap.put("password", password);
        jsonAsMap.put("role", role);
        return jsonAsMap;
    }

    public static Map<String, Object> userPayloadIncomplete(String username) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("username", username);
        jsonAsMap.put("password", "test12");
        return jsonAsMap;
    }

    public static Map<String, Object> userPayloadForUpdate() {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("password", "test13");
        return jsonAsMap;
    }

    public static Map<String, Object> productPayload(String productName, int cost, int amount) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("productName", productName);
        jsonAsMap.put("cost", cost);
        jsonAsMap.put("amountAvailable", amount);
        return jsonAsMap;
    }

    public static Map<String, Object> productPayloadIncomplete(String productName, int amount) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("productName", productName);
        jsonAsMap.put("amountAvailable", amount);
        return jsonAsMap;
    }

    public static Map<String, Object> depositPayload(long fives, long tens, long twenties, long fifties, long hundreds) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("fives", fives);
        jsonAsMap.put("tens", tens);
        jsonAsMap.put("twenties", twenties);
        jsonAsMap.put("fifties", fifties);
        jsonAsMap.put("hundreds", hundreds);
        return jsonAsMap;
    }

    public static Map<String, Object> buyPayload(List<Long> productIds) {
        Map<String, Object> jsonAsMap = new HashMap();
        jsonAsMap.put("productIds", productIds);
        return jsonAsMap;
    }

    public static Response createUser(Map<String, Object> jsonAsMap) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .post("/user");
    }

    public static Response getAllUsers(String username, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .when()
                .get("/user/");
    }

    public static Response getAllUsersWithoutAuth() {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .when()
                .get("/user/");
    }

    public static Response getUserByUsernameOrId(String username, String password, String searchingParameter) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .when()
                .get("/user/" + searchingParameter);
    }

    public static Response updateUser(Map<String, Object> jsonAsMap, User user, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(user.getUsername(), password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .put("/user/" + user.getId());
    }

    public static Response updateUserWithoutAuth(Map<String, Object> jsonAsMap, User user) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .put("/user/" + user.getId());
    }

    public static Response deleteUser(User user, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(user.getUsername(), password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .when()
                .delete("/user/" + user.getId());
    }

    public static Response deleteUserWithoutAuth(User user) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .when()
                .delete("/user/" + user.getId());
    }

    public static Response createProduct(Map<String, Object> jsonAsMap, String username, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .post("/product");
    }

    public static Response createProductWithoutAuth(Map<String, Object> jsonAsMap) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .post("/product");
    }

    public static Response readAllProducts() {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .when()
                .get("/product");
    }

    public static Response readProduct(long id) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .when()
                .get("/product/" + id);
    }

    public static Response updateProduct(Map<String, Object> jsonAsMap, User user, String password, long id) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(user.getUsername(), password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .put("/product/" + id);
    }

    public static Response updateProductWithoutAuth(Map<String, Object> jsonAsMap, long id) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .put("/product/" + id);
    }

    public static Response deleteProduct(User user, String password, long productId) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(user.getUsername(), password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .when()
                .delete("/product/" + productId);
    }

    public static Response deleteProductWithoutAuth(long productId) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .contentType("application/json")
                .when()
                .delete("/product/" + productId);
    }

    public static Response deposit(Map<String, Object> jsonAsMap, String username, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .post("/deposit");
    }

    public static Response buyByCart(Map<String, Object> jsonAsMap, String username, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .post("/buy");
    }

    public static Response buyByProductId(Map<String, Object> jsonAsMap, String username, String password, long productId) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .body(jsonAsMap)
                .when()
                .get("/buy/" + productId);
    }

    public static Response reset(String username, String password) {
        return given()
                .port(PORT)
                .baseUri(BASE_URL)
                .auth()
                .preemptive()
                .basic(username, password != null ? password : PASSWORD)
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType("application/json")
                .when()
                .get("/reset");
    }

    public static List<User> getUsersFromResponse(ObjectMapper objectMapper, Response response) throws JsonProcessingException {
        return objectMapper.readValue(response.getBody().prettyPrint(), new TypeReference<List<User>>(){});
    }

    public static User createAndGetUser(ObjectMapper objectMapper, Map<String, Object> jsonAsMap) throws JsonProcessingException {
        createUser(jsonAsMap);

        Response response = getAllUsers(jsonAsMap.get("username").toString(), PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        return users.stream().filter(u -> jsonAsMap.get("username").toString().equals(u.getUsername())).findFirst().get();
    }

    public static List<Product> getProductsFromResponse(ObjectMapper objectMapper, Response response) throws JsonProcessingException {
        return objectMapper.readValue(response.getBody().prettyPrint(), new TypeReference<List<Product>>(){});
    }

    public static Product getProductByName(ObjectMapper objectMapper, String username) throws JsonProcessingException {
        Response response = readAllProducts();
        List<Product> products = getProductsFromResponse(objectMapper, response);
        return products.stream().filter(p -> p.getProductName().equals(username)).findFirst().get();
    }
}
