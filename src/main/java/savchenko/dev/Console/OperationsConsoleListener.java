package savchenko.dev.Console;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.EnumMap;
import java.util.Arrays;

@Component
public class OperationsConsoleListener implements Runnable {

    private final Map<ConsoleOperationType, OperationCommand> commandMap;
    private final Scanner scanner = new Scanner(System.in);

    public OperationsConsoleListener(List<OperationCommand> commands) {
        this.commandMap = new EnumMap<>(ConsoleOperationType.class);
        for (OperationCommand command : commands) {
            commandMap.put(command.getOperationType(), command);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Please enter one of operation type:");
                Arrays.stream(ConsoleOperationType.values())
                        .forEach(System.out::println);
                String input = scanner.nextLine();

                ConsoleOperationType type = ConsoleOperationType.valueOf(input);
                OperationCommand command = commandMap.get(type);

                if (command == null) {
                    System.out.println("Unknown command");
                    continue;
                }

                command.execute();
                System.out.println("Operation completed successfully\n");

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}