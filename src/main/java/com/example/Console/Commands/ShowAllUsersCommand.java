package com.example.Console.Commands;

import com.example.Console.ConsoleOperationType;
import com.example.Console.OperationCommand;
import com.example.Services.AccountService;
import com.example.Services.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersCommand implements OperationCommand {

    private final UserService userService;
    private final AccountService accountService;

    public ShowAllUsersCommand(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        if (userService.getAllUsers().isEmpty()) {
            System.out.println("No users registered yet");
        } else {
            System.out.println("List of all users:");
            userService.getAllUsers().forEach(user -> {
                System.out.println(user);
                accountService.getAllAccountsByUserId(user.getId())
                        .forEach(acc -> System.out.println("  " + acc));
            });
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}