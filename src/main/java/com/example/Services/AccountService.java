package com.example.Services;

import com.example.Model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class AccountService {

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final BigDecimal defaultAmount;
    private final BigDecimal transferCommission;

    public AccountService(@Value("${account.default-amount}") BigDecimal defaultAmount,
                          @Value("${account.transfer-commission}") BigDecimal transferCommission) {
        this.defaultAmount = defaultAmount.setScale(2, RoundingMode.HALF_UP);
        this.transferCommission = transferCommission.setScale(2, RoundingMode.HALF_UP);
    }

    public Account createFirstAccount(Long userId) {
        Long accountId = idGenerator.getAndIncrement();
        Account account = new Account(userId, accountId, defaultAmount.setScale(2, RoundingMode.HALF_UP));
        accounts.put(accountId, account);
        return account;
    }

    public Account createAccount(Long userId) {
        Long accountId = idGenerator.getAndIncrement();
        Account account = new Account(userId, accountId, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        accounts.put(accountId, account);
        return account;
    }


    public Optional<Account> getAccountById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public List<Account> getAllAccountsByUserId(Long userId) {
        return accounts.values().stream()
                .filter(acc -> acc.getUserId().equals(userId))
                .sorted(Comparator.comparing(Account::getAccountId))
                .collect(Collectors.toList());
    }

    public void deposit(Long accountId, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + accountId + " does not exist"));
        account.setMoneyAmount(account.getMoneyAmount().add(amount).setScale(2, RoundingMode.HALF_UP));
    }

    public void withdraw(Long accountId, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        Account account = getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + accountId + " does not exist"));
        if (account.getMoneyAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough money on account with id: " + accountId);
        }
        account.setMoneyAmount(account.getMoneyAmount().subtract(amount).setScale(2, RoundingMode.HALF_UP));
    }

    public void transfer(Long sourceId, Long destinationId, BigDecimal amount) {
        //Normalizing amount
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        //Check if transferring to same account
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        //Getting source and destination accounts
        Account sourceAcc = getAccountById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + sourceId + " does not exist"));
        Account destinationAcc = getAccountById(destinationId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + destinationId + " does not exist"));

        //Calculating fee in case of transferring between different users
        BigDecimal fee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (!sourceAcc.getUserId().equals(destinationAcc.getUserId())) {
            fee = amount.multiply(transferCommission).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal totalWithdraw = amount.add(fee).setScale(2, RoundingMode.HALF_UP);


        //Check if source account has enough money
        if (sourceAcc.getMoneyAmount().compareTo(totalWithdraw) < 0) {
            throw new IllegalArgumentException("Not enough money on account with id: " + sourceId);
        }


        //Money transfer
        withdraw(sourceId, totalWithdraw);
        deposit(destinationId, amount);

    }

    public void deleteAccountById(Long accountId) {
        Account accountToClose = getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + accountId + " does not exist"));
        Long userId = accountToClose.getUserId();
        List<Account> userAccounts = getAllAccountsByUserId(userId);

        if (userAccounts.size() == 1) {
            throw new IllegalArgumentException("Cannot close the only account of user " + userId);
        }

        //Finding first account
        Account firstAccount = userAccounts.stream()
                .filter(acc -> !acc.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow();
        BigDecimal remainingAmount = accountToClose.getMoneyAmount();

        //deposit remaining amount to the first account
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            deposit(firstAccount.getAccountId(), remainingAmount);
        }
        accounts.remove(accountId);
    }

    public Boolean existsById(Long id) {
        return accounts.containsKey(id);
    }


}
