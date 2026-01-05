package com.example;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final AccountService accountService;

    public UserService(AccountService accountService) {
        this.accountService = accountService;
    }

    public User createUser(String username) {
        if (existsByName(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        //creating new user with auto-generated id
        Long userId = idGenerator.getAndIncrement();
        User user = new User(username, userId);
        users.put(userId, user);

        //creating first account with initial amount
        Account account = accountService.createFirstAccount(userId);
        return user;
    }

    public Boolean existsByName(String name) {
        return users.values().stream()
                .anyMatch(u -> name.equalsIgnoreCase(u.getName()));
    }

    public User getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + userId + " is not found");
        }
        return user;
    }



//    public Account createAccount(Long userId) {
//        Account account = accountService.createAccount(userId);
//        Long accountId = account.getAccountId();
//
//    }

}
