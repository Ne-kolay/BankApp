package savchenko.dev.Console.Commands;

import savchenko.dev.Console.ConsoleOperationType;
import savchenko.dev.Console.OperationCommand;
import savchenko.dev.Services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class TransferCommand implements OperationCommand {

    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public TransferCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.print("Enter source account ID: ");
        Long sourceId = Long.parseLong(scanner.nextLine());
        if (!accountService.existsById(sourceId)) {
            throw new IllegalArgumentException("Account with id " + sourceId + "does not exist");
        }

        System.out.print("Enter target account ID: ");
        Long targetId = Long.parseLong(scanner.nextLine());
        if (!accountService.existsById(targetId)) {
            throw new IllegalArgumentException("Account with id " + targetId + "does not exist");
        }

        System.out.print("Enter amount to transfer: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        accountService.transfer(sourceId, targetId, amount);
        System.out.println("Amount " + amount + " transferred from account ID "
                + sourceId + " to account ID " + targetId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
