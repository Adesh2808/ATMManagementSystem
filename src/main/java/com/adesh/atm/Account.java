package com.adesh.atm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String name;
    private final String pin;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String name, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
        this.balance = initialBalance;
        transactions.add(new Transaction(LocalDateTime.now(), "INIT", initialBalance, balance));
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid Amount");
        balance += amount;
        transactions.add(new Transaction(LocalDateTime.now(), "DEPOSIT", amount, balance));
    }

    public void withdraw(double amount) {
        if (amount <= 0 || amount > balance) throw new IllegalArgumentException("Insufficient balance");
        balance -= amount;
        transactions.add(new Transaction(LocalDateTime.now(), "WITHDRAW", amount, balance));
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getPin() { return pin; }
    public List<Transaction> getTransactions() { return transactions; }
}
