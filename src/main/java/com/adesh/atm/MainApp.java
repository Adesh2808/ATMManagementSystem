package com.adesh.atm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main JavaFX entry point for the ATM Management System.
 */
public class MainApp extends Application {

    /** Handles login & account‑level operations */
    private final AccountManager accountManager = new AccountManager();

    /** We keep a reference so we can swap scenes easily */
    private Stage stage;

    /* ──────────────────────────────
     *  JavaFX lifecycle
     * ────────────────────────────── */
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("ATM Management System");
        showLoginScene();          // first screen
        primaryStage.show();
    }

    /* ──────────────────────────────
     *  Login scene
     * ────────────────────────────── */
    private void showLoginScene() {
        Label accLabel = new Label("Account Number:");
        TextField accField = new TextField();
        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        Button loginBtn = new Button("Login");
        Label messageLabel = new Label();

        loginBtn.setOnAction(e -> {
            String acc = accField.getText().trim();
            String pin = pinField.getText().trim();
            accountManager.authenticate(acc, pin).ifPresentOrElse(
                    account -> showMainMenuScene(account),
                    () -> messageLabel.setText("❌ Invalid account or PIN")
            );
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(accLabel, 0, 0);
        grid.add(accField, 1, 0);
        grid.add(pinLabel, 0, 1);
        grid.add(pinField, 1, 1);
        grid.add(loginBtn, 1, 2);
        grid.add(messageLabel, 1, 3);

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
    }

    /* ──────────────────────────────
     *  Main menu after login
     * ────────────────────────────── */
    private void showMainMenuScene(Account account) {
        Label welcome = new Label("Welcome, " + account.getName()
                + " (" + account.getAccountNumber() + ")");

        Button depositBtn   = new Button("Deposit");
        Button withdrawBtn  = new Button("Withdraw");
        Button balanceBtn   = new Button("Check Balance");
        Button statementBtn = new Button("Mini Statement");
        Button logoutBtn    = new Button("Logout");

        depositBtn.setOnAction(e   -> showDepositScene(account));
        withdrawBtn.setOnAction(e  -> showWithdrawScene(account));
        balanceBtn.setOnAction(e   -> showBalanceScene(account));
        statementBtn.setOnAction(e -> showStatementScene(account));
        logoutBtn.setOnAction(e    -> showLoginScene());

        VBox vbox = new VBox(10, welcome,
                depositBtn, withdrawBtn, balanceBtn, statementBtn, logoutBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
    }

    /* ──────────────────────────────
     *  Deposit screen
     * ────────────────────────────── */
    private void showDepositScene(Account account) {
        Label amountLabel = new Label("Amount to deposit:");
        TextField amountField = new TextField();
        Button depositBtn = new Button("Deposit");
        Button backBtn = new Button("Back");
        Label msg = new Label();

        depositBtn.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText());
                account.deposit(amt);
                msg.setText("✅ Deposit successful! New balance: ₹" + account.getBalance());
            } catch (Exception ex) {
                msg.setText("❌ " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showMainMenuScene(account));

        VBox vbox = new VBox(10, amountLabel, amountField, depositBtn, backBtn, msg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 400, 300));
    }

    /* ──────────────────────────────
     *  Withdraw screen
     * ────────────────────────────── */
    private void showWithdrawScene(Account account) {
        Label amountLabel = new Label("Amount to withdraw:");
        TextField amountField = new TextField();
        Button withdrawBtn = new Button("Withdraw");
        Button backBtn = new Button("Back");
        Label msg = new Label();

        withdrawBtn.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(amountField.getText());
                account.withdraw(amt);
                msg.setText("✅ Withdrawal successful! New balance: ₹" + account.getBalance());
            } catch (Exception ex) {
                msg.setText("❌ " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showMainMenuScene(account));

        VBox vbox = new VBox(10, amountLabel, amountField, withdrawBtn, backBtn, msg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 400, 300));
    }

    /* ──────────────────────────────
     *  Balance screen
     * ────────────────────────────── */
    private void showBalanceScene(Account account) {
        Label balanceLabel = new Label("Current balance: ₹" + account.getBalance());
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showMainMenuScene(account));

        VBox vbox = new VBox(10, balanceLabel, backBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 400, 200));
    }

    /* ──────────────────────────────
     *  Mini‑statement screen
     * ────────────────────────────── */
    private void showStatementScene(Account account) {
        ListView<String> listView = new ListView<>();
        account.getTransactions()
                .forEach(t -> listView.getItems().add(t.toString()));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showMainMenuScene(account));

        VBox vbox = new VBox(10, new Label("Mini Statement"), listView, backBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 500, 400));
    }

    /* ──────────────────────────────
     *  Main method
     * ────────────────────────────── */
    public static void main(String[] args) {
        launch(args);
    }
}
