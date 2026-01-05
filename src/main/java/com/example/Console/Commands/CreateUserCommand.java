package com.example.Console.Commands;

import com.example.Console.ConsoleOperationType;
import com.example.Console.OperationCommand;
import com.example.Model.User;
import com.example.Services.AccountService;
import com.example.Services.UserService;
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