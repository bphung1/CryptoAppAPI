package crypto.service;

import crypto.dto.CoinMarkets;
import crypto.entity.Investment;
import crypto.entity.Portfolio;
import crypto.entity.Transaction;
import crypto.entity.User;

import java.util.List;

public interface CryptoService {
    User login(String username, String password);
    User createAccount(User user);
    Portfolio inputNonInvestedBalance(int userId, double deposit);
    Portfolio withdrawFromNonInvBal(int userId, double amount);
    Portfolio getPortfolio(int userId);
    List<Transaction> getTransactionByPortfolioId(int portfolioId);
    List<Investment> getInvestmentsByPortfolioId(int portfolioId);
    Transaction transactionForBuy(int portfolioId, Transaction transaction);
    List<CoinMarkets> rateForCrypto();
    Transaction transactionForSell(int portfolioId, Transaction transaction);

}
