package crypto.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Portfolio {
    private int portfolioId;
    private int userId;
    private BigDecimal investedTotalBalance;
    private BigDecimal nonInvestedBalance;

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getInvestedTotalBalance() {
        return investedTotalBalance;
    }

    public void setInvestedTotalBalance(BigDecimal investedTotalBalance) {
        this.investedTotalBalance = investedTotalBalance;
    }

    public BigDecimal getNonInvestedBalance() {
        return nonInvestedBalance;
    }

    public void setNonInvestedBalance(BigDecimal nonInvestedBalance) {
        this.nonInvestedBalance = nonInvestedBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return portfolioId == portfolio.portfolioId && userId == portfolio.userId && Objects.equals(investedTotalBalance, portfolio.investedTotalBalance) && Objects.equals(nonInvestedBalance, portfolio.nonInvestedBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioId, userId, investedTotalBalance, nonInvestedBalance);
    }
}
