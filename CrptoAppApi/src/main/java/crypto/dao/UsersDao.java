package crypto.dao;

import crypto.entity.User;
import org.springframework.dao.DataAccessException;

public interface UsersDao {
    User createUsers(User user) throws DataAccessException;
    User getUsers(String username, String password) throws DataAccessException;
    User getUserByUsername(String username) throws DataAccessException;
    User deleteUser(User user, int portfolioId) throws DataAccessException;
    User updateUser(User user) throws DataAccessException;
}
