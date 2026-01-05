package com.example.Console.Commands;

import com.example.Console.ConsoleOperationType;
import com.example.Console.OperationCommand;
import com.example.Services.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountCommand implements OperationCommand {

    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public CloseAccountCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.print("Enter account ID to close: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        if (!accountService.existsById(accountId)) {
            throw new IllegalArgumentException("Account with id " + accountId + "does not exist");
        }

        accountService.deleteAccountById(accountId);
        System.out.println("Account with ID " + accountId + " has been closed.");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
