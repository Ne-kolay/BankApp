package com.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

    private final Long id;
    private String name;
//    private final List<Long> accountIds = new ArrayList<>();


    public User(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Long> getAccountIds() {
//        return Collections.unmodifiableList(accountIds);
//    }
//    public void addAccountId(Long accountId) {
//        accountIds.add(accountId);
//    }
}
