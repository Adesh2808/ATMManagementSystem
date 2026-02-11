import java.sql.*;
import java.util.Scanner;

public class ATM{

    static Scanner sc = new Scanner(System.in);
    static Connection con;

    public static void main(String[] args) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/atm_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    "root",
                    "123456"
            );

            System.out.println("===== WELCOME TO ATM =====");

            String accNo = login();

            if (accNo != null) {
                menu(accNo);
            } else {
                System.out.println("Card Blocked!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- LOGIN ----------------
    static String login() throws Exception {
        int attempts = 3;

        while (attempts-- > 0) {
            System.out.print("Enter Account No: ");
            String accNo = sc.next();

            System.out.print("Enter PIN: ");
            int pin = sc.nextInt();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM accounts WHERE acc_no=? AND pin=?");
            ps.setString(1, accNo);
            ps.setInt(2, pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Welcome " + rs.getString("name"));
                return accNo;
            }

            System.out.println("Wrong Credentials. Left: " + attempts);
        }
        return null;
    }

    // ---------------- MENU ----------------
    static void menu(String accNo) throws Exception {

        while (true) {
            System.out.println("\n1.Check Balance");
            System.out.println("2.Deposit");
            System.out.println("3.Withdraw");
            System.out.println("4.Mini Statement");
            System.out.println("5.Change PIN");
            System.out.println("6.Exit");

            System.out.print("Choose: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> checkBalance(accNo);
                case 2 -> deposit(accNo);
                case 3 -> withdraw(accNo);
                case 4 -> miniStatement(accNo);
                case 5 -> changePin(accNo);
                case 6 -> System.exit(0);
                default -> System.out.println("Invalid");
            }
        }
    }

    // ---------------- BALANCE ----------------
    static void checkBalance(String accNo) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM accounts WHERE acc_no=?");
        ps.setString(1, accNo);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("Balance: ₹" + rs.getDouble(1));
        }
    }

    // ---------------- DEPOSIT ----------------
    static void deposit(String accNo) throws Exception {
        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE accounts SET balance=balance+? WHERE acc_no=?");
        ps.setDouble(1, amt);
        ps.setString(2, accNo);
        ps.executeUpdate();

        saveTransaction(accNo, "Deposit", amt);
        System.out.println("Deposited Successfully");
    }

    // ---------------- WITHDRAW ----------------
    static void withdraw(String accNo) throws Exception {

        double bal = getBalance(accNo);

        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        if (amt <= bal) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance=balance-? WHERE acc_no=?");
            ps.setDouble(1, amt);
            ps.setString(2, accNo);
            ps.executeUpdate();

            saveTransaction(accNo, "Withdraw", amt);
            System.out.println("Collect Cash");
        } else {
            System.out.println("Insufficient Balance");
        }
    }

    // ---------------- MINI STATEMENT ----------------
    static void miniStatement(String accNo) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT type,amount,date FROM transactions WHERE acc_no=? ORDER BY id DESC LIMIT 5");
        ps.setString(1, accNo);

        ResultSet rs = ps.executeQuery();
        System.out.println("---- Last Transactions ----");
        while (rs.next()) {
            System.out.println(rs.getString(1) + " ₹" + rs.getDouble(2) +
                    " | " + rs.getTimestamp(3));
        }
    }

    // ---------------- CHANGE PIN ----------------
    static void changePin(String accNo) throws Exception {
        System.out.print("Old PIN: ");
        int oldPin = sc.nextInt();

        PreparedStatement check = con.prepareStatement(
                "SELECT * FROM accounts WHERE acc_no=? AND pin=?");
        check.setString(1, accNo);
        check.setInt(2, oldPin);

        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            System.out.print("New PIN: ");
            int newPin = sc.nextInt();

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET pin=? WHERE acc_no=?");
            ps.setInt(1, newPin);
            ps.setString(2, accNo);
            ps.executeUpdate();

            System.out.println("PIN Changed");
        } else {
            System.out.println("Wrong Old PIN");
        }
    }

    // ---------------- HELPERS ----------------
    static double getBalance(String accNo) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM accounts WHERE acc_no=?");
        ps.setString(1, accNo);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    static void saveTransaction(String accNo, String type, double amt) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO transactions(acc_no,type,amount) VALUES(?,?,?)");
        ps.setString(1, accNo);
        ps.setString(2, type);
        ps.setDouble(3, amt);
        ps.executeUpdate();
    }
}

