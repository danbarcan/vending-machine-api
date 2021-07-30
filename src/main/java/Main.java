import com.sun.net.httpserver.HttpServer;
import controller.OperationController;
import controller.ProductController;
import controller.UserController;
import utils.DatabaseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        DatabaseUtils.createTables();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        new UserController(server).init();
        new ProductController(server).init();
        new OperationController(server).init();

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
