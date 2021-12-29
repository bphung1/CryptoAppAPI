package crypto.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Investment {
    private int investmentId;
    private int portfolioId;
    private String cryptoName;
    private BigDecimal investedAmount;
    private BigDecimal shares;

    public int getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(int investmentId) {
        this.investmentId = investmentId;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public BigDecimal getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(BigDecimal investedAmount) {
        this.investedAmount = investedAmount;
    }

    public BigDecimal getShares() {
        return shares;
    }

    public void setShares(BigDecimal shares) {
        this.shares = shares;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investment that = (Investment) o;
        return investmentId == that.investmentId && portfolioId == that.portfolioId && Objects.equals(cryptoName, that.cryptoName) && Objects.equals(investedAmount, that.investedAmount) && Objects.equals(shares, that.shares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(investmentId, portfolioId, cryptoName, investedAmount, shares);
    }
}
