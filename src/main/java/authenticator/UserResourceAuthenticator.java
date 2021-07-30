package authenticator;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import model.User;
import service.UserService;
import utils.Constants;

public class UserResourceAuthenticator extends BasicAuthenticator {
    private final UserService userService;

    public UserResourceAuthenticator() {
        super("myrealm");
        this.userService = new UserService();
    }
    /**
     * Creates a BasicAuthenticator for the given HTTP realm
     *
     * @param realm The HTTP Basic authentication realm
     * @throws NullPointerException if the realm is an empty string
     */
    public UserResourceAuthenticator(String realm) {
        super(realm);
        this.userService = new UserService();
    }

    @Override
    public Result authenticate(HttpExchange exchange) {
        if (Constants.HTTP_POST.equalsIgnoreCase(exchange.getRequestMethod())) {
            return new Authenticator.Success(new HttpPrincipal("anonymous", realm));
        } else if (Constants.HTTP_PUT.equalsIgnoreCase(exchange.getRequestMethod()) || Constants.HTTP_DELETE.equalsIgnoreCase(exchange.getRequestMethod())) {
            Authenticator.Result result = super.authenticate(exchange);
            if (result instanceof Authenticator.Success) {
                User user = userService.findUserByUsername(((Success) result).getPrincipal().getUsername());
                String parameter = exchange.getRequestURI().toString().replaceAll(Constants.USER_RESOURCE_URL + "/", "");
                try {
                    long userId = Long.parseLong(parameter);
                    if (userId == user.getId()) {
                        return result;
                    } else {
                        return new Authenticator.Failure(401);
                    }
                } catch (Exception e) {
                    return new Authenticator.Failure(401);
                }
            }
        }
        return super.authenticate(exchange);
    }

    @Override
    public boolean checkCredentials(String username, String pwd) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return false;
        }
        return username.equals(user.getUsername()) && pwd.equals(user.getPassword());
    }
}
