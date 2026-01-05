package com.example;

import com.example.Console.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.example");

        OperationsConsoleListener operationsConsoleListener = context.getBean(OperationsConsoleListener.class);
        operationsConsoleListener.run();


    }

}