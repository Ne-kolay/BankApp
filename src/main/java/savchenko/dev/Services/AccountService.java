package savchenko.dev.Services;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import savchenko.dev.TransactionHelper;
import savchenko.dev.Model.Account;
import savchenko.dev.Model.User;
import org.springframework.stereotype.Component;
import savchenko.dev.Repositories.UserRepository;
import savchenko.dev.Repositories.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Comparator;

@Component
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;
    private final BigDecimal defaultAmount;
    private final BigDecimal transferCommission;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          TransactionHelper transactionHelper,
                          SessionFactory sessionFactory,
                          @Value("${account.default-amount}") BigDecimal defaultAmount,
                          @Value("${account.transfer-commission}") BigDecimal transferCommission) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
        this.defaultAmount = defaultAmount.setScale(2, RoundingMode.HALF_UP);
        this.transferCommission = transferCommission.setScale(2, RoundingMode.HALF_UP);
    }

    //CREATE
    public Account createFirstAccount(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            User user = userRepository.findById(userId, session)
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " does not exist"));
            Account account = new Account(defaultAmount, user);
            return accountRepository.save(account, session);
        });
    }

    public Account createAccount(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            User user = userRepository.findById(userId, session)
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " does not exist"));
            Account account = new Account(BigDecimal.ZERO, user);
            return accountRepository.save(account, session);
        });
    }

    //READ
    public Account getAccountById(Long id) {
        return transactionHelper.executeInTransaction(session -> {
            return accountRepository.findById(id, session)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " does not exist"));
        });
    }

    public boolean existsById(Long accountId) {
        return transactionHelper.executeInTransaction(session -> {
            return accountRepository.existsById(accountId, session);
        });
    }

    public List<Account> getAllAccountsByUserId(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            return accountRepository.findAllByUserId(userId, session);
        });
    }

    //UPDATE
    public void deposit(Long accountId, BigDecimal amount) {
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        transactionHelper.executeInTransaction(session -> {
            Account account = accountRepository.findById(accountId, session)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + accountId + " does not exist"));

            account.setMoneyAmount(account.getMoneyAmount().add(normalizedAmount)
                    .setScale(2, RoundingMode.HALF_UP));
            accountRepository.update(account, session);
        });
    }

    public void withdraw(Long accountId, BigDecimal amount) {
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }

        transactionHelper.executeInTransaction(session -> {
            Account account = accountRepository.findById(accountId, session)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + accountId + " does not exist"));

            account.setMoneyAmount(account.getMoneyAmount().subtract(normalizedAmount)
                    .setScale(2, RoundingMode.HALF_UP));
            accountRepository.update(account, session);
        });
    }

    public void transfer(Long sourceId, Long destinationId, BigDecimal amount) {
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        if (normalizedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        transactionHelper.executeInTransaction(session -> {
            Account sourceAcc = accountRepository.findById(sourceId, session)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + sourceId + " does not exist"));
            Account destinationAcc = accountRepository.findById(destinationId, session)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + destinationId + " does not exist"));

            BigDecimal fee = BigDecimal.ZERO;
            if (!sourceAcc.getUser().getId().equals(destinationAcc.getUser().getId())) {
                fee = normalizedAmount.multiply(transferCommission)
                        .setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal totalAmount = normalizedAmount.add(fee)
                    .setScale(2, RoundingMode.HALF_UP);

            if (sourceAcc.getMoneyAmount().compareTo(totalAmount) < 0) {
                throw new IllegalArgumentException("Not enough money on account with id: " + sourceId);
            }

            decreaseBalance(sourceAcc, totalAmount);
            increaseBalance(destinationAcc, normalizedAmount);

            accountRepository.update(sourceAcc, session);
            accountRepository.update(destinationAcc, session);
        });
    }

    //DELETE
    public void deleteAccountById(Long accountId) {
        transactionHelper.executeInTransaction(session -> {
            Account accountToClose = accountRepository.findById(accountId, session)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Account with id " + accountId + " does not exist"));

            List<Account> userAccounts = accountRepository.findAllByUserId(
                    accountToClose.getUser().getId(), session);

            if (userAccounts.size() == 1) {
                throw new IllegalArgumentException(
                        "Cannot close the only account of user " + accountToClose.getUser().getId());
            }

            Account firstAccount = userAccounts.stream()
                    .filter(acc -> !acc.getId().equals(accountId))
                    .min(Comparator.comparing(Account::getId))
                    .orElseThrow();

            BigDecimal remainingAmount = accountToClose.getMoneyAmount();
            if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                increaseBalance(firstAccount, remainingAmount);
                accountRepository.update(firstAccount, session);
            }
            accountRepository.delete(accountToClose, session);
        });
    }

    //PRIVATE HELPER METHODS
    private void increaseBalance(Account account, BigDecimal amount) {
        account.setMoneyAmount( account.getMoneyAmount().add(amount).setScale(2, RoundingMode.HALF_UP));
    }
    private void decreaseBalance(Account account, BigDecimal amount) {
        account.setMoneyAmount(account.getMoneyAmount().subtract(amount).setScale(2, RoundingMode.HALF_UP));
    }
}
