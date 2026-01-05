package com.example.Console.Commands;

import com.example.Console.ConsoleOperationType;
import com.example.Console.OperationCommand;
import com.example.Services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class WithdrawCommand implements OperationCommand {

    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public WithdrawCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.print("Enter account ID to withdraw from: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        if (!accountService.existsById(accountId)) {
            throw new IllegalArgumentException("Account with id " + accountId + "does not exist");
        }

        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        accountService.withdraw(accountId, amount);
        System.out.println("Amount " + amount + " withdrawn from account ID: " + accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
