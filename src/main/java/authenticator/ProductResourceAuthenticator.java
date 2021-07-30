package authenticator;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import model.Product;
import model.User;
import service.ProductService;
import service.UserService;
import utils.Constants;

public class ProductResourceAuthenticator extends BasicAuthenticator {
    private final UserService userService;
    private final ProductService productService;

    /**
     * Creates a BasicAuthenticator for the given HTTP realm
     *
     * @param realm The HTTP Basic authentication realm
     * @throws NullPointerException if the realm is an empty string
     */
    public ProductResourceAuthenticator(String realm) {
        super(realm);
        this.userService = new UserService();
        this.productService = new ProductService();
    }

    @Override
    public Result authenticate(HttpExchange exchange) {
        if (Constants.HTTP_GET.equalsIgnoreCase(exchange.getRequestMethod())) {
            return new Success(new HttpPrincipal("anonymous", realm));
        } else if (Constants.HTTP_POST.equalsIgnoreCase(exchange.getRequestMethod()) || Constants.HTTP_PUT.equalsIgnoreCase(exchange.getRequestMethod()) || Constants.HTTP_DELETE.equalsIgnoreCase(exchange.getRequestMethod())) {
            Result result = super.authenticate(exchange);
            if (result instanceof Success) {
                User user = userService.findUserByUsername(((Success) result).getPrincipal().getUsername());
                if (Constants.HTTP_POST.equalsIgnoreCase(exchange.getRequestMethod())) {
                    return user.getRole().equals(User.Role.SELLER) ? result : new Failure(401);
                } else {
                    try {
                        String parameter = exchange.getRequestURI().toString().replaceAll(Constants.PRODUCT_RESOURCE_URL + "/", "");
                        try {
                            long productId = Long.parseLong(parameter);
                            Product product = productService.findProductById(productId);
                            if (product != null && product.getSellerId() == user.getId()) {
                                return result;
                            } else {
                                return new Authenticator.Failure(401);
                            }
                        } catch (Exception e) {
                            return new Authenticator.Failure(401);
                        }
                    } catch (Exception e) {
                        return new Failure(401);
                    }
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
