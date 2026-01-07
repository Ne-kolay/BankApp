package savchenko.dev.Console.Commands;

import savchenko.dev.Console.ConsoleOperationType;
import savchenko.dev.Console.OperationCommand;
import savchenko.dev.Model.Account;
import savchenko.dev.Services.AccountService;
import savchenko.dev.Services.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateAccountCommand implements OperationCommand {

    private final AccountService accountService;
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public CreateAccountCommand(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void execute() {
        System.out.print("Enter the user id for which to create an account: ");
        Long userId = Long.parseLong(scanner.nextLine());

        userService.getById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + userId));

        Account account = accountService.createAccount(userId);
        System.out.println("New account created with ID: " + account.getAccountId());
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
