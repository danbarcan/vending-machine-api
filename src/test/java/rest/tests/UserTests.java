package rest.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static rest.tests.TestUtils.*;

public class UserTests {
    private static String username;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createUser_success() {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        Response response = createUser(jsonAsMap);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("User successfully created!", response.getBody().prettyPrint());
    }

    @Test
    public void createUser_sameUsername_fails() {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = createUser(jsonAsMap);

        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("User not created!", response.getBody().prettyPrint());
    }

    @Test
    public void createUser_badParameters_fails() {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayloadIncomplete(username);

        createUser(jsonAsMap);

        Response response = createUser(jsonAsMap);

        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals("Invalid request body!", response.getBody().prettyPrint());
    }

    @Test
    public void readAllUsers_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);

        Assertions.assertEquals(200, response.statusCode());

        List<User> users = getUsersFromResponse(objectMapper, response);
        Assertions.assertTrue(users.size() > 0);
        Assertions.assertTrue(users.stream().anyMatch(user -> username.equals(user.getUsername())));
    }

    @Test
    public void readUser_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getUserByUsernameOrId(username, PASSWORD, username);

        Assertions.assertEquals(200, response.statusCode());

        User userFound = objectMapper.readValue(response.getBody().prettyPrint(), User.class);
        Assertions.assertEquals(username, userFound.getUsername());
    }

    @Test
    public void readAllUsers_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsersWithoutAuth();

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void readAllUsers_badPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, RANDOM);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void updateUser_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        jsonAsMap = userPayloadForUpdate();
        response = updateUser(jsonAsMap, user, null);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Update successful", response.getBody().prettyPrint());
    }

    @Test
    public void updateUser_anotherUser_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        jsonAsMap = userPayloadForUpdate();
        response = updateUser(jsonAsMap, user, null);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void updateUser_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        jsonAsMap = userPayloadForUpdate();
        response = updateUserWithoutAuth(jsonAsMap, user);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void updateUser_badPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        jsonAsMap = userPayloadForUpdate();
        response = updateUser(jsonAsMap, user, RANDOM);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteUser_success() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        jsonAsMap = userPayloadForUpdate();
        response = deleteUser(user, null);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Delete successful", response.getBody().prettyPrint());
    }

    @Test
    public void deleteUser_anotherUser_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        response = deleteUser(user, null);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteUser_unauthenticated_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        response = deleteUserWithoutAuth(user);

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void deleteUser_badPassword_fails() throws JsonProcessingException {
        username = "test" + new Random().nextInt(1000000000);
        Map<String, Object> jsonAsMap = userPayload(username, PASSWORD, BUYER);

        createUser(jsonAsMap);

        Response response = getAllUsers(username, PASSWORD);
        List<User> users = getUsersFromResponse(objectMapper, response);
        User user = users.stream().filter(u -> username.equals(u.getUsername())).findFirst().get();

        user.setId(user.getId() + 1);
        response = deleteUser(user, RANDOM);

        Assertions.assertEquals(401, response.statusCode());
    }
}