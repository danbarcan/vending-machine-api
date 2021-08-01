import com.sun.net.httpserver.HttpServer;
import controller.OperationController;
import controller.ProductController;
import controller.UserController;
import utils.DatabaseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, SQLException {
        DatabaseUtils.createTables();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        new UserController(server).init();
        new ProductController(server).init();
        new OperationController(server).init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Server is shutting down...");
            server.stop(2);
            logger.info("Server stopped");
        }));

        logger.info("Server is ready to handle requests at " + serverPort);

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
