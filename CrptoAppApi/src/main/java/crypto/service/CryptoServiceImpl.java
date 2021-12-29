package crypto.service;

import crypto.dao.InvestmentDao;
import crypto.dao.PortfolioDao;
import crypto.dao.TransactionDao;
import crypto.dao.UsersDao;
import crypto.dto.CoinMarkets;
import crypto.entity.Investment;
import crypto.entity.Portfolio;
import crypto.entity.Transaction;
import crypto.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
    public Transaction transactionForBuy(int portfolioId, Transaction transaction) {
        try {
            Portfolio portfolio = portfolioDao.getPortfolioById(portfolioId);
            if(portfolio.getNonInvestedBalance().compareTo(transaction.getTransactionAmount()) >= 0 &&
                transaction.getTransactionAmount().compareTo(BigDecimal.valueOf(0)) > 0
            ) {
                CoinMarkets crypt = getCrypto(transaction.getCryptoName());
                BigDecimal convertBalanceToShare = transaction.getTransactionAmount()
                    .divide(crypt.getCurrent_price(), 8, RoundingMode.HALF_DOWN);

                Investment investment = createInvestment(portfolioId, convertBalanceToShare, transaction, crypt);
                investmentDao.addInvestment(portfolioId,investment);

                Transaction completeTransaction = autoGenerateValuesForTransaction(transaction, portfolioId, convertBalanceToShare, crypt);
                transactionDao.addTransaction(completeTransaction);
                updatePortfolio(transaction, portfolio);
                return transaction;
            }
            return null;
        }catch (DataAccessException | NullPointerException ex ){
            return null;
        }
    }

    @Override
    public List<CoinMarkets> rateForCrypto() {
        String[] cryptoNames = {"bitcoin", "dogecoin", "ethereum", "litecoin", "cardano"};
//        String[] cryptoNames = {"dogecoin"};

        List<CoinMarkets> coinMarkets = new ArrayList<>();

        for (String name : cryptoNames) {
            coinMarkets.add(getCrypto(name));
        }

        return coinMarkets;
    }

    @Override
    public Transaction transactionForSell(int portfolioId, Transaction userInputTransaction) {
        BigDecimal remainingUserInputShares = userInputTransaction.getShares();
        try {
            List<Investment> allActiveInvestments = investmentDao.getAllInvestments(portfolioId);
            List<Investment> filteredInv = filterInvestments(allActiveInvestments, userInputTransaction.getCryptoName());

            if (userInputTransaction.getShares().compareTo(getTotalShares(filteredInv)) <= 0 &&
                    userInputTransaction.getShares().compareTo(new BigDecimal("0.00")) > 0
            ) {
                CoinMarkets selectedCrypto = getCrypto(userInputTransaction.getCryptoName());
                Transaction newTransaction = autoGenerateValuesForTransaction(userInputTransaction, portfolioId,
                        userInputTransaction.getShares(), selectedCrypto);

                BigDecimal convertToAmount = selectedCrypto.getCurrent_price().multiply(userInputTransaction.getShares());
                newTransaction.setTransactionAmount(convertToAmount);

                transactionDao.addTransaction(newTransaction);

                Portfolio portfolio = portfolioDao.getPortfolio(portfolioId);
                portfolio.setNonInvestedBalance(portfolio.getNonInvestedBalance().add(convertToAmount));
                portfolio.setInvestedTotalBalance(portfolio.getInvestedTotalBalance().subtract(convertToAmount));
                portfolioDao.updatePortfolioBalance(portfolio);

                for (Investment inv : filteredInv) {
                    if (inv.getShares().compareTo(remainingUserInputShares) <= 0) {
                        remainingUserInputShares = remainingUserInputShares.subtract(inv.getShares());
                        investmentDao.deleteInvestment(inv);
                    } else if (inv.getShares().compareTo(remainingUserInputShares) > 0){
                        inv.setShares(inv.getShares().subtract(remainingUserInputShares));
                        inv.setInvestedAmount(selectedCrypto.getCurrent_price().multiply(inv.getShares()));
                        remainingUserInputShares = remainingUserInputShares.subtract(remainingUserInputShares);
                        investmentDao.updateInvestment(inv);
                        break;
                    }
                }
            } else {
                return null;
            }
            return userInputTransaction;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Investment> filterInvestments(List<Investment> investments, String cryptoName) {
        return investments.stream().filter((p)-> p.getCryptoName()
                        .equalsIgnoreCase(cryptoName))
                .collect(Collectors.toList());
    }

    private BigDecimal getTotalShares(List<Investment> investments) {
        BigDecimal total = new BigDecimal(0.00);
        for (Investment inv : investments) {
            total = total.add(inv.getShares());
        }
        return total;
    }

    private CoinMarkets getCrypto(String symbol) {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=" +
                symbol + "&order=market_cap_desc&per_page=100&page=1&sparkline=false";

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .build();

        CoinMarkets response = webClient.get()
                .retrieve()
                .bodyToFlux(CoinMarkets.class)
                .blockLast();

        return response;
    }

    private Transaction autoGenerateValuesForTransaction(Transaction transaction, int portfolioId,
                                                         BigDecimal convertBalanceToShare, CoinMarkets crypt) {
        transaction.setPortfolioId(portfolioId);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setShares(convertBalanceToShare);
        transaction.setCryptoRate(crypt.getCurrent_price().setScale(8, RoundingMode.HALF_DOWN));

        return transaction;
    }

    private Investment createInvestment(int portfolioId, BigDecimal convertBalanceToShare,
                                        Transaction transaction, CoinMarkets crypt) {
        Investment investment = new Investment();
        investment.setPortfolioId(portfolioId);
        investment.setShares(convertBalanceToShare);
        investment.setCryptoName(transaction.getCryptoName());
        investment.setInvestedAmount(transaction.getTransactionAmount());

        return investment;
    }

    private Portfolio updatePortfolio(Transaction transaction, Portfolio portfolio) {
        BigDecimal newInvestedTotalBalance = portfolio.getInvestedTotalBalance().add(transaction.getTransactionAmount());
        BigDecimal newNonInvestedBalance = portfolio.getNonInvestedBalance().subtract(transaction.getTransactionAmount());
        portfolio.setInvestedTotalBalance(newInvestedTotalBalance);
        portfolio.setNonInvestedBalance(newNonInvestedBalance);
        portfolioDao.updatePortfolioBalance(portfolio);
        return portfolio;
    }

    //keep in case we need to change external API
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
