package rest.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import model.Product;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import payload.BuyResponse;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static rest.tests.TestUtils.*;

public class OperationTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDepositBuyAndResetFlow() throws JsonProcessingException {
        int random = new Random().nextInt(1000000000);
        String username = "test" + random;
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, SELLER);
        User seller = createAndGetUser(objectMapper, jsonAsMap);

        String chocoName = "Lindt Chocolate" + random;
        String biscName = "Oreo Biscuits" + random;
        String sodaName = "Coca-Cola Soda" + random;
        jsonAsMap = productPayload(chocoName, 80, 10);
        createProduct(jsonAsMap, seller.getUsername(), null);
        Product chocolate = getProductByName(objectMapper, chocoName);

        jsonAsMap = productPayload(biscName, 30, 10);
        createProduct(jsonAsMap, seller.getUsername(), null);
        Product biscuits = getProductByName(objectMapper, biscName);

        jsonAsMap = productPayload(sodaName, 30, 10);
        createProduct(jsonAsMap, seller.getUsername(), null);
        Product soda = getProductByName(objectMapper, sodaName);

        username = "test" + new Random().nextInt(1000000000);
        jsonAsMap = userPayload(username, PASSWORD, BUYER);
        User buyer = createAndGetUser(objectMapper, jsonAsMap);

        // deposit 200 cents
        jsonAsMap = depositPayload(4, 1, 1, 1, 1);
        Response response = deposit(jsonAsMap, buyer.getUsername(), null);

        Assertions.assertEquals(200, response.getStatusCode());

        buyer = objectMapper.readValue(getUserByUsernameOrId(buyer.getUsername(), PASSWORD, buyer.getUsername()).getBody().prettyPrint(), User.class);
        Assertions.assertEquals(200l, buyer.getDeposit());

        jsonAsMap = buyPayload(List.of(chocolate.getId(), soda.getId(), biscuits.getId()));
        response = buyByCart(jsonAsMap, buyer.getUsername(), null);

        Assertions.assertEquals(200, response.getStatusCode());

        BuyResponse buyResponse = objectMapper.readValue(response.getBody().prettyPrint(), BuyResponse.class);
        Assertions.assertEquals(140, buyResponse.getTotalSpent());
        Assertions.assertEquals(Map.of(10, 1l, 50, 1l), buyResponse.getChange());
        Assertions.assertEquals(3, buyResponse.getProductsPurchased().size());
        Assertions.assertTrue(buyResponse.getProductsPurchased().contains(chocolate));
        Assertions.assertTrue(buyResponse.getProductsPurchased().contains(biscuits));
        Assertions.assertTrue(buyResponse.getProductsPurchased().contains(soda));

        response = reset(buyer.getUsername(), null);

        Assertions.assertEquals(200, response.getStatusCode());

        buyer = objectMapper.readValue(getUserByUsernameOrId(buyer.getUsername(), PASSWORD, buyer.getUsername()).getBody().prettyPrint(), User.class);
        Assertions.assertEquals(0l, buyer.getDeposit());

        // deposit 50 cents
        jsonAsMap = depositPayload(0, 0, 0, 1, 0);
        response = deposit(jsonAsMap, buyer.getUsername(), null);

        Assertions.assertEquals(200, response.getStatusCode());

        buyer = objectMapper.readValue(getUserByUsernameOrId(buyer.getUsername(), PASSWORD, buyer.getUsername()).getBody().prettyPrint(), User.class);
        Assertions.assertEquals(50l, buyer.getDeposit());

        response = buyByProductId(jsonAsMap, buyer.getUsername(), null, biscuits.getId());

        Assertions.assertEquals(200, response.getStatusCode());

        buyResponse = objectMapper.readValue(response.getBody().prettyPrint(), BuyResponse.class);
        Assertions.assertEquals(30, buyResponse.getTotalSpent());
        Assertions.assertEquals(Map.of(20, 1l), buyResponse.getChange());
        Assertions.assertEquals(1, buyResponse.getProductsPurchased().size());
        Assertions.assertTrue(buyResponse.getProductsPurchased().contains(biscuits));
    }
}
