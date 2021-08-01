package authenticator;

import com.sun.net.httpserver.BasicAuthenticator;
import exception.ResourceNotFoundException;
import lombok.Getter;
import model.User;
import service.UserService;

import java.sql.SQLException;

@Getter
public class BuyerAuthenticator extends BasicAuthenticator {
    private final UserService userService;

    /**
     * Creates a BasicAuthenticator for the given HTTP realm
     *
     * @param realm The HTTP Basic authentication realm
     * @throws NullPointerException if the realm is an empty string
     */
    public BuyerAuthenticator(String realm) throws SQLException {
        super(realm);
        this.userService = new UserService();
    }

    @Override
    public boolean checkCredentials(String username, String pwd) {
        try {
            User user = userService.findUserByUsername(username);
            return username.equals(user.getUsername()) && pwd.equals(user.getPassword()) && user.getRole().equals(User.Role.BUYER);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
