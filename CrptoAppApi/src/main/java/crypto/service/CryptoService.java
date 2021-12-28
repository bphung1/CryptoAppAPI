package crypto.service;

import crypto.dto.Crypto;
import crypto.entity.Investment;
import crypto.entity.Portfolio;
import crypto.entity.Transaction;
import crypto.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CryptoService {
    User login(String username, String password);
    User createAccount(User user);
    Portfolio inputNonInvestedBalance(int userId, double deposit);
    Portfolio withdrawFromNonInvBal(int userId, double amount);
    Portfolio getPortfolio(int userId);
    List<Transaction> getTransactionByPortfolioId(int portfolioId);
    List<Investment> getInvestmentsByPortfolioId(int portfolioId);
    Transaction addTransaction(int portfolioId, Transaction transaction);


    //update portfolio for balance
    //add helper method for total invested balance and update portfolio total balance
}
