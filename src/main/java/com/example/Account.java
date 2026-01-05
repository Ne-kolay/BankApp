package com.example;
import java.math.BigDecimal;

public class Account {

    private final Long accountId;
    private final Long userId;
    private BigDecimal moneyAmount;

    public Account(Long userId, Long accountId, BigDecimal initialAmount) {
        this.accountId = accountId;
        this.userId = userId;
        this.moneyAmount = initialAmount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = moneyAmount;
    }
}

