package crypto.dao;

import crypto.entity.Investment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class InvestmentDaoImpl implements InvestmentDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public static final class investmentMapper implements RowMapper<Investment> {
        public Investment mapRow(ResultSet resultSet, int i) throws SQLException {
            Investment investments=new Investment();
            investments.setInvestmentId(resultSet.getInt("investmentId"));
            investments.setPortfolioId(resultSet.getInt("portfolioId"));
            investments.setCryptoName(resultSet.getString("cryptoName"));
            investments.setInvestedAmount(resultSet.getBigDecimal("investedAmount"));
            investments.setShares(resultSet.getBigDecimal("shares"));
            return investments;
        }
    }

    @Override
    public List<Investment> getAllInvestments(int portfolioId) throws DataAccessException {
        final String SELECT_Investment = "SELECT * FROM Investment where portfolioId = ?;";
        return jdbcTemplate.query(SELECT_Investment, new investmentMapper(),portfolioId);
    }

    @Override
    @Transactional
    public Investment addInvestment(int portfolioId, Investment investment) throws DataAccessException {
        final String INSERT_NEW_INVESTMENT="INSERT INTO Investment(portfolioId, cryptoName, investedAmount, shares)" +
                "values (?,?,?,?);";
        jdbcTemplate.update(INSERT_NEW_INVESTMENT,portfolioId,
                investment.getCryptoName(),investment.getInvestedAmount(),investment.getShares());
        int newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        investment.setInvestmentId(newId);
        return investment;
    }

    @Override
    public Investment deleteInvestment(Investment investment) throws DataAccessException {
        final String DELETE_INVESTMENT = "DELETE FROM Investment WHERE investmentId = ?;";
        jdbcTemplate.update(DELETE_INVESTMENT,investment.getInvestmentId());
        return investment;
    }

    @Override
    public Investment updateInvestment(Investment investment) {
        final String UPDATE_INVESTMENT = "UPDATE Investment SET investedAmount = ?, shares = ? where investmentId = ?;";
//        final String UPDATE_INVESTMENT = "UPDATE Investment SET shares = ? WHERE investmentId = ?;";
        jdbcTemplate.update(UPDATE_INVESTMENT, investment.getInvestedAmount(),investment.getShares(),investment.getInvestmentId());
//        jdbcTemplate.update(UPDATE_INVESTMENT, investment.getShares(), investment.getInvestmentId());
        return investment;
    }


}
