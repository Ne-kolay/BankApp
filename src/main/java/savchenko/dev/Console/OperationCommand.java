package savchenko.dev.Console;

public interface OperationCommand {
    void execute();
    ConsoleOperationType getOperationType();
}
