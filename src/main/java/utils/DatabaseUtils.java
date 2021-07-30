package utils;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Product;
import model.User;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseUtils {
    private static final String DATABASE_URL = "jdbc:postgresql://postgres:5432/vending-machine";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "admin";
    private static DataSourceConnectionSource connectionSource = null;

    public static DataSourceConnectionSource getDataSourceConnection() {
        if (connectionSource == null) {
            PGSimpleDataSource source = new PGSimpleDataSource();
            source.setURL(DATABASE_URL);
            source.setUser(DATABASE_USER);
            source.setPassword(DATABASE_PASSWORD);

            try {
                connectionSource = new DataSourceConnectionSource(source, DATABASE_URL);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        return connectionSource;
    }

//    public static void createTables() {
//        findAllClassesUsingClassLoader("model").forEach(clazz -> {
//            System.out.println("CLAZZ:" + clazz);
//            try {
//                TableUtils.createTableIfNotExists(getDataSourceConnection(), clazz);
//                System.out.println("Created table for class: " + clazz);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                if (!e.getCause().getMessage().contains("already exists")) {
//                    e.printStackTrace();//TODO logs
//                }
//            }
//        });
//    }

    public static void createTables() {
        try {
            TableUtils.createTableIfNotExists(getDataSourceConnection(), User.class);
            System.out.println("Created table for class: " + Product.class);
        } catch (SQLException e) {
            e.printStackTrace();
            if (!e.getCause().getMessage().contains("already exists")) {
                e.printStackTrace();//TODO logs
            }
        }
    }

    // https://www.baeldung.com/java-find-all-classes-in-package#1-system-class-loader
    private static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class") && !line.contains("$"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
            e.printStackTrace();//TODO logs
        }
        return null;
    }
}
