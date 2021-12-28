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
public class PortfolioDaoImpl implements PortfolioDao{
    @Autowired
    JdbcTemplate jdbc;
//    public static final class usersMapper implements RowMapper<Users> {
//        @Override
//        public Users mapRow(ResultSet resultSet, int i) throws SQLException {
//            Users users = new Users();
//            users.setUserid(resultSet.getInt("userId"));
//            users.setUsername(resultSet.getString("username"));
//            users.setPassword(resultSet.getString("password"));
//            users.setBalance(resultSet.getBigDecimal("balance"));
//            users.setEmail(resultSet.getString("email"));
//            return users;
//        }
//    }
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
//    public static final class activeInvestmentMapper implements RowMapper<Investments> {
//        @Override
//        public Investments mapRow(ResultSet resultSet, int i) throws SQLException {
//            Investments investments = new Investments();
//            investments.setInvestmentId(resultSet.getInt("investmentId"));
//            investments.setPortfolioId(resultSet.getInt("portfolioId"));
//            investments.setCryptoName(resultSet.getString("cryptoName"));
//            investments.setInvestedAmount(resultSet.getBigDecimal("investmentAmount"));
//            investments.setShares(resultSet.getInt("shares"));
//            return investments;
//        }
//    }

//    public static final class TransactionsMapper implements RowMapper<Transactions> {
//        @Override
//        public Transactions mapRow(ResultSet resultSet, int i) throws SQLException {
//            Transactions transactions = new Transactions();
//            transactions.setTransactionId(resultSet.getInt("transactionId"));
//            transactions.setPortfolioId(resultSet.getInt("portfolioId"));
//            //transactions.setTimestamp(resultSet.getTimestamp("timeStamp"));
//            transactions.setTransactionAmount(resultSet.getBigDecimal("transactionAmount"));
//            transactions.setCryptoName(resultSet.getString("cryptoName"));
//            transactions.setTransactionAmount(resultSet.getBigDecimal("transactionType"));
//
//            transactions.setShares(resultSet.getInt("shares"));
//            return transactions;
//        }
//    }

    @Override
    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio) {
        final String INSERT_PORTFOLIO = "INSERT INTO Portfolio (userId,investedTotalBalance,nonInvestedBalance)" + "VALUES(?,?,?);";
        jdbc.update(INSERT_PORTFOLIO,portfolio.getUserId(),portfolio.getInvestedTotalBalance().toString(),portfolio.getNonInvestedBalance());
        int newId = jdbc.queryForObject("SELECT MAX(portfolioId) FROM Portfolio;", Integer.class);
        portfolio.setPortfolioId(newId);

        return portfolio;
    }

    @Override
    public Portfolio getPortfolio(int userId) {
        final String GET_PORTFOLIO = "SELECT * from Portfolio where userId = ?;";
        return jdbc.queryForObject(GET_PORTFOLIO, new portfolioMapper(), userId);
    }

    @Override
    public Portfolio updatePortfolioBalance(Portfolio portfolio) {
        final String UPDATE_INVESTED_BALANCE_PORTFOLIO = "UPDATE Portfolio set investedTotalBalance = ?, nonInvestedBalance = ? where userId = ?;";
        jdbc.update(UPDATE_INVESTED_BALANCE_PORTFOLIO, portfolio.getInvestedTotalBalance(),portfolio.getNonInvestedBalance(),portfolio.getUserId());
        return portfolio;
    }

    @Override
    public Portfolio getPortfolioById(int portfolioId) {
        final String GET_PORTFOLIO_BY_ID = "SELECT * from Portfolio where portfolioId = ?;";
        return jdbc.queryForObject(GET_PORTFOLIO_BY_ID,new portfolioMapper(),portfolioId);
    }


//    @Override
//    public List<Transactions> getActiveInvestment(int portfolioId) {
//        final String SELECT_ACTIVE_INVESTMENTS = "SELECT cryptoName,transactionAmount, shares FROM Transactions where portfolioId = ?";
//        return jdbc.query(SELECT_ACTIVE_INVESTMENTS, new TransactionsMapper(), portfolioId);
//
//    }

//    @Override
//    public Portfolio getInvestmentBalance(int userId) {//meaning?
//        final String SELECT_INVESTMENT_BALANCE = "SELECT investedTotalBalance FROM Portfolio where userId = ?";
//        return jdbc.queryForObject(SELECT_INVESTMENT_BALANCE, new portfolioMapper(), userId);
//    }
//
//    @Override
//    public Portfolio nonInvestmentBalance(int userId) {
//        final String SELECT_NON_INVESTMENT_BALANCE = "SELECT nonInvestedTotalBalance FROM Portfolio where userId = ?";
//        return jdbc.queryForObject(SELECT_NON_INVESTMENT_BALANCE, new portfolioMapper(), userId);
//    }

//    @Override
//    public  List<Transactions> getBuyHistory(int portfolioId) {
//        final String SELECT_BUY_HISTORY = "SELECT * FROM Transactions where transactionType = BUY and portfolioId = ?";
//        return jdbc.query(SELECT_BUY_HISTORY, new TransactionsMapper(), portfolioId);
//    }
//
//    @Override
//    public  List<Transactions> getSellHistory(int portfolioId) {
//        final String SELECT_SELL_HISTORY = "SELECT * FROM Transactions where transactionType = SELL and portfolioId = ?";
//        return jdbc.query(SELECT_SELL_HISTORY, new TransactionsMapper(), portfolioId);
//    }

}
