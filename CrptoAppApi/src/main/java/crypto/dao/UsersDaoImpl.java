package crypto.dao;

import crypto.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UsersDaoImpl implements UsersDao{
    @Autowired
    JdbcTemplate jdbc;

    @Override
    @Transactional
    public User createUsers(User user) throws DataAccessException {
        final String INSERT_NEW_USER = "INSERT INTO User(username, password, email) VALUES (?, ?, ?);";

        jdbc.update(INSERT_NEW_USER,
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        user.setUserid(newId);

        return user;
    }

    @Override
    public User getUsers(String username, String password) throws DataAccessException {
        final String SELECT_BY_USER_AND_PASSWORD = "SELECT * FROM User WHERE username = ? AND password = ?;";
        User user = jdbc.queryForObject(SELECT_BY_USER_AND_PASSWORD, new UserMapper(), username, password);
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws DataAccessException {
        final String GET_USER_BY_USERNAME = "SELECT * FROM User WHERE username = ?";
        User user = jdbc.queryForObject(GET_USER_BY_USERNAME, new UserMapper(), username);
        return user;
    }

    @Override
    @Transactional
    public User deleteUser(User user, int portfolioId) throws DataAccessException {
        final String DELETE_INVESTMENT_BY_PORTFOLIO = "DELETE FROM Investment WHERE portfolioId = ?;";
        final String DELETE_TRANSACTION_BY_PORTFOLIO = "DELETE FROM Transaction WHERE portfolioId = ?;";
        final String DELETE_PORTFOLIO = "DELETE FROM Portfolio WHERE userId = ?;";
        final String DELETE_USER = "DELETE FROM User WHERE userid = ?;";

        jdbc.update(DELETE_INVESTMENT_BY_PORTFOLIO, portfolioId);
        jdbc.update(DELETE_TRANSACTION_BY_PORTFOLIO, portfolioId);
        jdbc.update(DELETE_PORTFOLIO, user.getUserid());
        jdbc.update(DELETE_USER, user.getUserid());

        return user;
    }

    @Override
    public User updateUser(User user) throws DataAccessException {
        final String UPDATE_USER = "UPDATE User SET password = ? WHERE userId = ?";
        jdbc.update(UPDATE_USER, user.getPassword(), user.getUserid());
        return user;
    }

    public static final class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int index) throws SQLException {
            User user = new User();
            user.setUserid(rs.getInt("userid"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));

            return user;
        }
    }
}
