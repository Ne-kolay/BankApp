package savchenko.dev.Console.Commands;

import savchenko.dev.Console.ConsoleOperationType;
import savchenko.dev.Console.OperationCommand;
import savchenko.dev.Services.AccountService;
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
