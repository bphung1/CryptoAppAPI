package crypto.service;

import crypto.dao.InvestmentDao;
import crypto.dao.PortfolioDao;
import crypto.dao.TransactionDao;
import crypto.dao.UsersDao;
import crypto.dto.Crypto;
import crypto.entity.Investment;
import crypto.entity.Portfolio;
import crypto.entity.Transaction;
import crypto.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@Repository
public class CryptoServiceImpl implements CryptoService{
    @Autowired
    UsersDao usersDao;

    @Autowired
    PortfolioDao portfolioDao;

    @Autowired
    TransactionDao transactionDao;

    @Autowired
    InvestmentDao investmentDao;

    @Override
    public User login(String username, String password) {
        try {
            return usersDao.getUsers(username, password);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public User createAccount(User user) {
        try {
            User newUser = usersDao.createUsers(user);
            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setUserId(newUser.getUserid());
            newPortfolio.setInvestedTotalBalance(new BigDecimal("0.00"));
            newPortfolio.setNonInvestedBalance(new BigDecimal("0.00"));
            portfolioDao.createPortfolio(newPortfolio);
            return newUser;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Portfolio inputNonInvestedBalance(int userId, double deposit) {
       Portfolio user = portfolioDao.getPortfolio(userId);
       BigDecimal currentBal = user.getNonInvestedBalance();
        if(deposit > 0){
            BigDecimal update = currentBal.add(BigDecimal.valueOf(deposit));
            user.setNonInvestedBalance(update);
        }
            return portfolioDao.updatePortfolioBalance(user);
    }

    @Override
    public Portfolio withdrawFromNonInvBal(int userId, double amount) {
        Portfolio user = portfolioDao.getPortfolio(userId);
        BigDecimal currentBal = user.getNonInvestedBalance();
        if(amount > 0 && currentBal.compareTo(BigDecimal.valueOf(amount)) >= 0){

            //we should check if currentBal - amount not less than 0 ?
            BigDecimal update = currentBal.subtract(BigDecimal.valueOf(amount));
            user.setNonInvestedBalance(update);
        }
        return portfolioDao.updatePortfolioBalance(user);

    }

    @Override
    public Portfolio getPortfolio(int userId) {
        try {
            return portfolioDao.getPortfolio(userId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Transaction> getTransactionByPortfolioId(int portfolioId) {

        try {
            return transactionDao.getTransactionsForPortfolio(portfolioId);
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Override
    public List<Investment> getInvestmentsByPortfolioId(int portfolioId) {
        try {
            return investmentDao.getAllInvestments(portfolioId);
        }catch (DataAccessException ex){
            return null;
        }

    }

    @Override
    public Transaction addTransaction(int portfolioId,Transaction transaction) {
       try {
           Portfolio portfolio = portfolioDao.getPortfolioById(portfolioId);
           if(portfolio.getNonInvestedBalance().compareTo(transaction.getTransactionAmount()) >= 0 &&
                transaction.getTransactionAmount().compareTo(BigDecimal.valueOf(0)) > 0
           ) {
               transaction.setPortfolioId(portfolioId);
               transaction.setTimestamp(LocalDateTime.now());
               Crypto crypt = rateForCrypto(transaction.getCryptoName());

               BigDecimal convertBalanceToShare = transaction.getTransactionAmount()
                       .divide(crypt.getRate(), 8, RoundingMode.HALF_DOWN);

              Investment investment=new Investment();
              investment.setPortfolioId(portfolioId);
              investment.setShares(convertBalanceToShare);
              investment.setCryptoName(transaction.getCryptoName());
              investment.setCryptoRate(crypt.getRate().setScale(8,RoundingMode.HALF_DOWN));
              investment.setInvestedAmount(transaction.getTransactionAmount());
              investmentDao.addInvestment(portfolioId,investment);


               transaction.setShares(convertBalanceToShare);
               transaction.setCryptoRate(crypt.getRate().setScale(8, RoundingMode.HALF_DOWN));
               transactionDao.addTransaction(transaction);
               updatePortfolio(transaction, portfolio);
               return transaction;
           }
           return null;
       }catch (DataAccessException | NullPointerException ex ){
           return null;
       }
    }

    private Portfolio updatePortfolio(Transaction transaction, Portfolio portfolio) {
        BigDecimal newInvestedTotalBalance = portfolio.getInvestedTotalBalance().add(transaction.getTransactionAmount());
        BigDecimal newNonInvestedBalance = portfolio.getNonInvestedBalance().subtract(transaction.getTransactionAmount());
        portfolio.setInvestedTotalBalance(newInvestedTotalBalance);
        portfolio.setNonInvestedBalance(newNonInvestedBalance);
        portfolioDao.updatePortfolioBalance(portfolio);
        return portfolio;
    }

    private Crypto rateForCrypto(String symbol) {
        String url = "https://rest.coinapi.io/v1/exchangerate/" + symbol + "/USD";
        String[] apiKeyAndValue = externalAPIKey();

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(apiKeyAndValue[0], apiKeyAndValue[1])
                .build();

        Crypto response = webClient.get()
                .retrieve()
                .bodyToMono(Crypto.class)
                .block();

        return response;
    }

    private String[] externalAPIKey() {
        try {
            InputStream input = new FileInputStream("src/main/resources/externalAPI.properties");
            Properties prop = new Properties();

            prop.load(input);

            String[] apiKeyAndValue = {prop.getProperty("keyName"), prop.getProperty("keyVal")};
            return apiKeyAndValue;
        } catch (IOException io) {
            System.out.println(io);
            return null;
        }
    }

}
