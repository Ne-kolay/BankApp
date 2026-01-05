package com.example.Console.Commands;

import com.example.Console.ConsoleOperationType;
import com.example.Console.OperationCommand;
import com.example.Services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class DepositCommand implements OperationCommand {

    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public DepositCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.print("Enter account ID: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        if (!accountService.existsById(accountId)) {
            throw new IllegalArgumentException("Account with id " + accountId + "does not exist");
        }

        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        accountService.deposit(accountId, amount);
        System.out.println("Amount " + amount + " deposited to account ID: " + accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
