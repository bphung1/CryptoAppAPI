package crypto.dao;

import crypto.entity.Portfolio;

import java.util.List;

public interface PortfolioDao {
    Portfolio createPortfolio(Portfolio portfolio);
    Portfolio getPortfolio (int userId);
    Portfolio updatePortfolioBalance(Portfolio portfolio);
    Portfolio getPortfolioById(int portfolioId);



//    List<Transactions> getActiveInvestment (int portfolioId);
//    Portfolio getInvestmentBalance (int userId);
//    Portfolio nonInvestmentBalance (int userId);
//    List<Transactions> getBuyHistory(int portfolioId);
//    List<Transactions> getSellHistory(int portfolioId);
}
