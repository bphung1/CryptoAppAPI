package crypto.dao;

import crypto.entity.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class PortfolioDaoImpl implements PortfolioDao {
    @Autowired
    JdbcTemplate jdbc;

    public static final class portfolioMapper implements RowMapper<Portfolio> {
        @Override
        public Portfolio mapRow(ResultSet resultSet, int i) throws SQLException {
            Portfolio portfolio = new Portfolio();
            portfolio.setPortfolioId(resultSet.getInt("portfolioId"));
            portfolio.setUserId(resultSet.getInt("userId"));
            portfolio.setInvestedTotalBalance(resultSet.getBigDecimal("investedTotalBalance"));
            portfolio.setNonInvestedBalance(resultSet.getBigDecimal("nonInvestedBalance"));
            return portfolio;
        }
    }

    @Override
    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio) {
        final String INSERT_PORTFOLIO = "INSERT INTO Portfolio (userId, investedTotalBalance, nonInvestedBalance)"
                + "VALUES(?,?,?) RETURNING portfolioId;";
        int newId = jdbc.queryForObject(INSERT_PORTFOLIO, Integer.class,
                portfolio.getUserId(), portfolio.getInvestedTotalBalance(), portfolio.getNonInvestedBalance());
//        int newId = jdbc.queryForObject("SELECT MAX(portfolioId) FROM Portfolio;", Integer.class);
        portfolio.setPortfolioId(newId);

        return portfolio;
    }

    @Override
    public Portfolio getPortfolioByUserId(int userId) {
        final String GET_PORTFOLIO = "SELECT * from Portfolio where userId = ?;";
        return jdbc.queryForObject(GET_PORTFOLIO, new portfolioMapper(), userId);
    }

    @Override
    public Portfolio getPortfolioByPortfolioId(int portfolioId) {
        final String GET_PORTFOLIO = "SELECT * from Portfolio where portfolioId = ?;";
        return jdbc.queryForObject(GET_PORTFOLIO, new portfolioMapper(), portfolioId);
    }

    @Override
    public Portfolio updatePortfolioBalance(Portfolio portfolio) {
        final String UPDATE_INVESTED_BALANCE_PORTFOLIO = "UPDATE Portfolio set investedTotalBalance = ?, nonInvestedBalance = ? where userId = ?;";
        jdbc.update(UPDATE_INVESTED_BALANCE_PORTFOLIO, portfolio.getInvestedTotalBalance(), portfolio.getNonInvestedBalance(), portfolio.getUserId());
        return portfolio;
    }

    @Override
    public Portfolio getPortfolioById(int portfolioId) {
        final String GET_PORTFOLIO_BY_ID = "SELECT * from Portfolio where portfolioId = ?;";
        return jdbc.queryForObject(GET_PORTFOLIO_BY_ID, new portfolioMapper(), portfolioId);
    }
}