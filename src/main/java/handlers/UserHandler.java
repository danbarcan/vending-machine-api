package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
import model.User;
import payload.UserRequest;
import payload.UserUpdateRequest;
import service.UserService;
import utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class UserHandler extends BaseHandler {
    private final UserService userService;
    private final ObjectMapper mapper;

    public UserHandler() throws SQLException {
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.resourceUrl = Constants.USER_RESOURCE_URL + "/";
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        String respText;
        UserRequest userRequest = mapper.readValue(exchange.getRequestBody(), UserRequest.class);
        if (userRequest.isValid()) {
            userService.createUser(userRequest);
            respText = "User successfully created!";
            exchange.sendResponseHeaders(201, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
        }
    }

    @Override
    protected void handleGetAll(HttpExchange exchange, OutputStream output) throws IOException {
        byte[] userJson = mapper.writeValueAsBytes(userService.findAll().stream().map(User::toUserResponse).collect(Collectors.toList()));
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, userJson.length);
        output.write(userJson);
        output.flush();
    }

    @Override
    protected void handleGetOneByParameter(HttpExchange exchange, OutputStream output, String parameter) throws IOException {
        User user;
        try {
            long id = parseParameterToLong(parameter);
            user = userService.findUserById(id);
        } catch (ResourceNotFoundException | InvalidParameterException e) {
            user = userService.findUserByUsername(parameter);
        }

        byte[] userJson = mapper.writeValueAsBytes(user.toUserResponse());
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, userJson.length);
        output.write(userJson);

        output.flush();
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        String respText;

        long userId = parseParameterToLong(parameter);
        UserUpdateRequest userRequest = mapper.readValue(exchange.getRequestBody(), UserUpdateRequest.class);
        if (userRequest != null && userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            if (userService.updateUser(userId, userRequest) >= 0) {
                respText = "Update successful";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
            } else {
                throw new ResourceNotUpdatedException("User not updated!");
            }
        } else {
            throw new InvalidRequestBodyException();
        }

        output.write(respText.getBytes());
        output.flush();
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        String parameter = exchange.getRequestURI().toString().replaceAll(resourceUrl, "");
        OutputStream output = exchange.getResponseBody();

        long userId = Long.parseLong(parameter);
        if (userService.deleteUserById(userId) >= 0) {
            String respText = "Delete successful";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            output.write(respText.getBytes());
        } else {
            throw new ResourceNotDeletedException("User not deleted!");
        }

        output.flush();
    }
}
