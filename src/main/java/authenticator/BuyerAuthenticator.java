package authenticator;

import com.sun.net.httpserver.BasicAuthenticator;
import model.User;
import service.UserService;

public class BuyerAuthenticator extends BasicAuthenticator {
    private final UserService userService;

    public BuyerAuthenticator() {
        super("myrealm");
        this.userService = new UserService();
    }

    /**
     * Creates a BasicAuthenticator for the given HTTP realm
     *
     * @param realm The HTTP Basic authentication realm
     * @throws NullPointerException if the realm is an empty string
     */
    public BuyerAuthenticator(String realm) {
        super(realm);
        this.userService = new UserService();
    }

    @Override
    public boolean checkCredentials(String username, String pwd) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return false;
        }
        return username.equals(user.getUsername()) && pwd.equals(user.getPassword()) && user.getRole().equals(User.Role.BUYER);
    }
}
