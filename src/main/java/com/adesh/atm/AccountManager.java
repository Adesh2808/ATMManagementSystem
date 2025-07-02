package com.adesh.atm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountManager {
    private final Map<String, Account> accounts = new HashMap<>();

    public AccountManager() {
        accounts.put("1234", new Account("1234", "Adesh", "1111", 10000));
        accounts.put("5678", new Account("5678", "Test User", "2222", 5000));
    }

    public Optional<Account> authenticate(String accNo, String pin) {
        Account acc = accounts.get(accNo);
        return (acc != null && acc.getPin().equals(pin)) ? Optional.of(acc) : Optional.empty();
    }
}
