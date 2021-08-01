package service;

import com.j256.ormlite.dao.Dao;
import dao.DaoFactory;
import exception.ResourceNotDeletedException;
import exception.ResourceNotFoundException;
import exception.ResourceNotUpdatedException;
import model.User;
import payload.UserRequest;
import payload.UserUpdateRequest;

import java.sql.SQLException;
import java.util.List;

import static utils.Constants.USER_NOT_FOUND;

public class UserService {
    private Dao<User, Long> userDao;

    public UserService() throws SQLException {
        this.userDao = DaoFactory.getUserDao();
    }

    public List<User> findAll() throws ResourceNotFoundException {
        try {
            List<User> users = userDao.queryForAll();
            if (users == null || users.isEmpty()) {
                throw new ResourceNotFoundException("No users found!");
            }
            return users;
        } catch (SQLException e) {
            throw new ResourceNotFoundException("No users found!", e);
        }
    }

    public User findUserByUsername(String username) throws ResourceNotFoundException {
        try {
            List<User> users = userDao.queryForEq("username", username);
            if (users == null || users.isEmpty()) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            return users.get(0);
        } catch (SQLException e) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, e);
        }
    }

    public User findUserById(Long id) throws ResourceNotFoundException {
        try {
            User user = userDao.queryForId(id);
            if (user == null) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            return user;
        } catch (SQLException e) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, e);
        }
    }

    public User createUser(UserRequest userRequest) throws ResourceNotUpdatedException {
        User user = User.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).role(userRequest.getRole()).build();

        try {
            user = userDao.createIfNotExists(user);
            if (user == null) {
                throw new ResourceNotUpdatedException("User not created!");
            }
            return user;
        } catch (SQLException e) {
            throw new ResourceNotUpdatedException("User not created!", e);
        }
    }

    public int updateUser(Long userId, UserUpdateRequest user) throws ResourceNotFoundException, ResourceNotUpdatedException {
        try {
            User existingUser = userDao.queryForId(userId);
            if (existingUser == null) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            existingUser.setPassword(user.getPassword());
            return updateUser(existingUser);
        } catch (SQLException e) {
            throw new ResourceNotUpdatedException(USER_NOT_FOUND, e);
        }
    }

    public int updateUser(User existingUser) throws SQLException {
        return userDao.update(existingUser);
    }

    public int deleteUserById(Long id) throws ResourceNotDeletedException {
        try {
            return userDao.deleteById(id);
        } catch (SQLException e) {
            throw new ResourceNotDeletedException("User could not be deleted!", e);
        }
    }

    public int resetUserDeposit(String username) throws ResourceNotFoundException, ResourceNotUpdatedException {
        User user = findUserByUsername(username);
        user.setDeposit(0);

        try {
            return updateUser(user);
        } catch (SQLException e) {
            throw new ResourceNotUpdatedException("Deposit not reset!", e);
        }
    }

    public int addCoinsToUserDeposit(String username, Long toAdd) throws ResourceNotFoundException, ResourceNotUpdatedException {
        User user = findUserByUsername(username);
        user.setDeposit(user.getDeposit() + toAdd);

        try {
            return updateUser(user);
        } catch (SQLException e) {
            throw new ResourceNotUpdatedException("Deposit was not completed!", e);
        }
    }
}
