package savchenko.dev;

import savchenko.dev.Console.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("savchenko.dev");

        OperationsConsoleListener operationsConsoleListener = context.getBean(OperationsConsoleListener.class);
        Thread consoleListener = new Thread(operationsConsoleListener);
        consoleListener.start();
    }
}