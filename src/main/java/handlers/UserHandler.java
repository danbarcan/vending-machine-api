package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.User;
import payload.UserRequest;
import payload.UserResponse;
import payload.UserUpdateRequest;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class UserHandler extends BaseHandler {
    public static final String USER_NOT_FOUND = "User not found!";
    private final UserService userService;
    private final ObjectMapper mapper;

    public UserHandler() {
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.USER_RESOURCE_URL + "/";
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        String respText;
        UserRequest userRequest = mapper.readValue(exchange.getRequestBody(), UserRequest.class);
        if (userRequest.isValid()) {
            User user = userService.createUser(userRequest);
            if (user != null) {
                respText = "User successfully created!";
                exchange.sendResponseHeaders(201, respText.getBytes().length);
            } else {
                respText = "User not created!";
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
        List<UserResponse> users = userService.findAll().stream().map(User::toUserResponse).collect(Collectors.toList());
        if (users != null && !users.isEmpty()) {
            byte[] userJson = mapper.writeValueAsBytes(users);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, userJson.length);
            output.write(userJson);
        } else {
            String respText = "No users found!";
            exchange.sendResponseHeaders(404, respText.getBytes().length);
            output.write(respText.getBytes());
        }
        output.flush();
    }

    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        User user;
        try {
            long id = Long.parseLong(parameter);
            user = userService.findUserById(id);
        } catch (Exception e) {
            user = userService.findUserByUsername(parameter);
        }
        if (user != null) {
            byte[] userJson = mapper.writeValueAsBytes(user.toUserResponse());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, userJson.length);
            output.write(userJson);
        } else {
            String respText = USER_NOT_FOUND;
            exchange.sendResponseHeaders(404, respText.getBytes().length);
            output.write(respText.getBytes());
        }
        output.flush();
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        String respText;
        if (parameter == null || parameter.isBlank()) {
            respText = "Invalid parameter!";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
        } else {
            try {
                long userId = Long.parseLong(parameter);
                UserUpdateRequest userRequest = mapper.readValue(exchange.getRequestBody(), UserUpdateRequest.class);
                if (userRequest != null && userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
                    if (userService.updateUser(userId, userRequest) >= 0) {
                        respText = "Update successful";
                        exchange.sendResponseHeaders(200, respText.getBytes().length);
                    } else {
                        respText = USER_NOT_FOUND;
                        exchange.sendResponseHeaders(404, respText.getBytes().length);
                    }
                } else {
                respText = "Invalid request body!";
                exchange.sendResponseHeaders(400, respText.getBytes().length);
            }
            } catch (Exception e) {
                respText = USER_NOT_FOUND;
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
            String respText = "Invalid parameter!";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
            output.write(respText.getBytes());
        } else {
            try {
                long userId = Long.parseLong(parameter);
                if (userService.deleteUserById(userId) >= 0) {
                    String respText = "Delete successful";
                    exchange.sendResponseHeaders(200, respText.getBytes().length);
                    output.write(respText.getBytes());
                } else {
                    String respText = USER_NOT_FOUND;
                    exchange.sendResponseHeaders(404, respText.getBytes().length);
                    output.write(respText.getBytes());
                }
            } catch (Exception e) {
                String respText = USER_NOT_FOUND;
                exchange.sendResponseHeaders(404, respText.getBytes().length);
                output.write(respText.getBytes());
            }
        }
        output.flush();
    }
}
