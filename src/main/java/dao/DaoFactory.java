package dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import model.Product;
import model.User;
import utils.DatabaseUtils;

import java.sql.SQLException;


public class DaoFactory {
    public static Dao<User, Long> getUserDao() throws SQLException {
        return DaoManager.createDao(DatabaseUtils.getDataSourceConnection(), User.class);
    }

    public static Dao<Product, Long> getProductDao() throws SQLException {
        return DaoManager.createDao(DatabaseUtils.getDataSourceConnection(), Product.class);
    }
}
