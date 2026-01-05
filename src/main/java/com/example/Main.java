package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.example");

        UserService userService = context.getBean(UserService.class);
        AccountService accountService = context.getBean(AccountService.class);

        User nik = userService.createUser("Nik");
        accountService.createAccount(1L);
        accountService.createAccount(1L);

        accountService.getAllAccountsByUserId(nik.getId())
                .stream()
                .forEach(acc -> System.out.println(acc.getAccountId()));


        accountService.transfer(1L, 2L, BigDecimal.valueOf(100));
        Account acc1 = accountService.getAccountById(1L).orElseThrow();
        Account acc2 = accountService.getAccountById(2L).orElseThrow();

        System.out.println(acc1.getMoneyAmount());
        System.out.println(acc2.getMoneyAmount());

        accountService.deleteAccountById(2L);

        System.out.println(acc1.getMoneyAmount());



    }

}