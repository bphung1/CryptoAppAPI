package crypto.dao;

import crypto.entity.Investment;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface InvestmentDao {
    List<Investment> getAllInvestments(int portfolioId) throws DataAccessException;
    Investment addInvestment(int portfolioId,Investment investment) throws DataAccessException;
    Investment deleteInvestment(Investment investment) throws DataAccessException;

}
