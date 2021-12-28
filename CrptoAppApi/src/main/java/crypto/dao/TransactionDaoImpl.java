package crypto.dao;

import com.mysql.cj.xdevapi.Row;
import crypto.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class TransactionDaoImpl implements TransactionDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    @Transactional
    public Transaction addTransaction(Transaction transaction) {
        final String INSERT_TRANSACTION = "INSERT INTO Transaction(portfolioId, timestamp, transactionAmount, " +
                                            "cryptoName, transactionType, shares, cryptoRate) VALUES (?,?,?,?,?,?,?);";

        jdbc.update(INSERT_TRANSACTION,
                transaction.getPortfolioId(),
                Timestamp.valueOf(transaction.getTimestamp()),
                transaction.getTransactionAmount(),
                transaction.getCryptoName(),
                transaction.getTransactionType(),
                transaction.getShares(),
                transaction.getCryptoRate()
        );

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        transaction.setTransactionId(newId);

        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsForPortfolio(int portfolioId) {

        final String SELECT_TRANSACTIONS_FOR_PORTFOLIO = "SELECT * FROM Transaction "
                + "WHERE portfolioId = ?;";

        List<Transaction> transactions = jdbc.query(SELECT_TRANSACTIONS_FOR_PORTFOLIO, new TransactionMapper(), portfolioId);

        return transactions;
    }

    public static final class TransactionMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet rs, int index) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(rs.getInt("transactionId"));
            transaction.setPortfolioId(rs.getInt("portfolioId"));
            transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            transaction.setTransactionAmount(rs.getBigDecimal("transactionAmount"));
            transaction.setCryptoName(rs.getString("cryptoName"));
            transaction.setTransactionType(rs.getString("transactionType"));
            transaction.setShares(rs.getBigDecimal("shares"));
            transaction.setCryptoRate(rs.getBigDecimal("cryptoRate"));

            return transaction;

        }

    }
}
