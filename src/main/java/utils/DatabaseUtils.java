package utils;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Product;
import model.User;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseUtils {
    private static final String DATABASE_URL = System.getenv("DATASOURCE_URL");
    private static final String DATABASE_USER = System.getenv("DATASOURCE_USERNAME");
    private static final String DATABASE_PASSWORD = System.getenv("DATASOURCE_PASSWORD");
    private static DataSourceConnectionSource connectionSource = null;
    private static PGSimpleDataSource dataSource = null;

    public static DataSourceConnectionSource getDataSourceConnection() {
        if (connectionSource == null) {
            try {
                connectionSource = new DataSourceConnectionSource(getDataSource(), DATABASE_URL);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        return connectionSource;
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new PGSimpleDataSource();
            dataSource.setURL(DATABASE_URL);
            dataSource.setUser(DATABASE_USER);
            dataSource.setPassword(DATABASE_PASSWORD);
        }

        return dataSource;
    }

    public static void createTables() throws SQLException {
        try {
            TableUtils.createTableIfNotExists(getDataSourceConnection(), User.class);
            TableUtils.createTableIfNotExists(getDataSourceConnection(), Product.class);
            Logger.getLogger(DatabaseUtils.class.getName()).info("Created tables");
        } catch (SQLException e) {
            if (!e.getCause().getMessage().contains("already exists")) {
                throw e;
            }
        }
    }
}
