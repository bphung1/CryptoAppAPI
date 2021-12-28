package crypto.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private int transactionId;
    private int portfolioId;
    private LocalDateTime timestamp;
    private BigDecimal transactionAmount;
    private String cryptoName;
    private String transactionType;
    private BigDecimal shares;
    private BigDecimal cryptoRate;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getShares() {
        return shares;
    }

    public void setShares(BigDecimal shares) {
        this.shares = shares;
    }

    public BigDecimal getCryptoRate() {
        return cryptoRate;
    }

    public void setCryptoRate(BigDecimal cryptoRate) {
        this.cryptoRate = cryptoRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId && portfolioId == that.portfolioId && Objects.equals(timestamp, that.timestamp) && Objects.equals(transactionAmount, that.transactionAmount) && Objects.equals(cryptoName, that.cryptoName) && Objects.equals(transactionType, that.transactionType) && Objects.equals(shares, that.shares) && Objects.equals(cryptoRate, that.cryptoRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, portfolioId, timestamp, transactionAmount, cryptoName, transactionType, shares, cryptoRate);
    }
}
