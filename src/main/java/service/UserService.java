package service;

import com.j256.ormlite.dao.Dao;
import dao.DaoFactory;
import model.User;
import payload.UserRequest;
import payload.UserUpdateRequest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UserService {
    private Dao<User, Long> userDao;

    public UserService() {
        try {
            this.userDao = DaoFactory.getUserDao();
        } catch (SQLException e) {
            e.printStackTrace();//TODO
        }
    }

    public List<User> findAll() {
        try {
            return userDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public User findUserByUsername(String username) {
        try {
            List<User> users = userDao.queryForEq("username", username);
            return users != null && !users.isEmpty() ? users.get(0) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findUserById(Long id) {
        try {
            return userDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User createUser(UserRequest userRequest) {
        User user = User.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).role(userRequest.getRole()).build();

        try {
            return userDao.createIfNotExists(user);
        } catch (SQLException e) {
            e.printStackTrace();//todo
            return null;
        }
    }

    public int updateUser(Long userId, UserUpdateRequest user) {
        try {
            User existingUser = userDao.queryForId(userId);
            if (existingUser == null) {
                return -1;
            }
            existingUser.setPassword(user.getPassword());
            return updateUser(existingUser);
        } catch (SQLException e) {
            e.printStackTrace();//todo
            return -2;
        }
    }

    public int updateUser(User existingUser) throws SQLException {
        return userDao.update(existingUser);
    }

    public int deleteUserById(Long id) {
        try {
            return userDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int resetUserDeposit(String username) {
        User user = findUserByUsername(username);
        user.setDeposit(0);

        try {
            return updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }

    public int addCoinsToUserDeposit(String username, Long toAdd) {
        User user = findUserByUsername(username);
        user.setDeposit(user.getDeposit() + toAdd);

        try {
            return updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
