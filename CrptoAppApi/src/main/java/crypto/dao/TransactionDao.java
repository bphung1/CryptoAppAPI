package crypto.dao;

import crypto.entity.Transaction;

import java.util.List;

public interface TransactionDao {

    Transaction addTransaction(Transaction transaction);
    List<Transaction> getTransactionsForPortfolio(int portfolioId);

}
