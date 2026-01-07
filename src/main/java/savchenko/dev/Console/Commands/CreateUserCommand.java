package savchenko.dev.Console.Commands;

import savchenko.dev.Console.ConsoleOperationType;
import savchenko.dev.Console.OperationCommand;
import savchenko.dev.Model.User;
import savchenko.dev.Services.AccountService;
import savchenko.dev.Services.UserService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {

    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public CreateUserCommand(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.print("Enter login for new user:");
        String name = scanner.nextLine();

        User user = userService.createUser(name);
        System.out.println("User created: id=" + user.getId() + ", name=" + user.getName());
        System.out.println("Account list: ");
        accountService.getAllAccountsByUserId(user.getId())
                .stream()
                .forEach(account -> System.out.println(account.toString()));
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}