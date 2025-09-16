import java.util.Scanner;
import java.util.List;

public class InteractiveBank {
    private static BankService bankService = new BankService();
    private static User loggedInUser = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            if (loggedInUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n--- Welcome to Concurrent Bank ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: handleRegister(); break;
            case 2: handleLogin(); break;
            case 3: System.exit(0); break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n--- Hello, " + loggedInUser.getUsername() + " ---");
        System.out.println("1. Create New Account");
        System.out.println("2. View My Accounts");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer Funds");
        System.out.println("6. View Transaction History");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: handleCreateAccount(); break;
            case 2: handleViewAccounts(); break;
            case 3: handleDeposit(); break;
            case 4: handleWithdraw(); break;
            case 5: handleTransfer(); break;
            case 6: handleViewHistory(); break;
            case 7: loggedInUser = null; break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (bankService.registerUser(username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists.");
        }
    }

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        loggedInUser = bankService.login(username, password);
        if (loggedInUser == null) {
            System.out.println("Invalid credentials.");
        } else {
            System.out.println("Login successful.");
        }
    }

    private static void handleCreateAccount() {
        System.out.print("Enter initial deposit amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        Account account = bankService.createAccount(loggedInUser.getUsername(), amount);
        System.out.println("Account created successfully. Your new account number is: " + account.getAccountNumber());
    }

    private static void handleViewAccounts() {
        List<Account> accounts = bankService.getAccountsForUser(loggedInUser.getUsername());
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts.");
            return;
        }
        System.out.println("--- Your Accounts ---");
        for (Account acc : accounts) {
            System.out.printf("Account Number: %s | Balance: $%.2f%n", acc.getAccountNumber(), acc.getBalance());
        }
    }

    private static void handleDeposit() {
        System.out.print("Enter account number to deposit into: ");
        String accNum = scanner.nextLine();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        Account acc = bankService.getAccount(accNum);
        if (acc != null && acc.getOwnerUsername().equals(loggedInUser.getUsername())) {
            acc.deposit(amount);
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Invalid account number or you do not own this account.");
        }
    }
    
    private static void handleWithdraw() {
        System.out.print("Enter account number to withdraw from: ");
        String accNum = scanner.nextLine();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        Account acc = bankService.getAccount(accNum);
        if (acc != null && acc.getOwnerUsername().equals(loggedInUser.getUsername())) {
            if (acc.withdraw(amount)) {
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Withdrawal failed. Insufficient funds.");
            }
        } else {
            System.out.println("Invalid account number or you do not own this account.");
        }
    }

    private static void handleTransfer() {
        System.out.print("Enter your account number to transfer from: ");
        String fromAccNum = scanner.nextLine();
        System.out.print("Enter the recipient's account number: ");
        String toAccNum = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Account fromAcc = bankService.getAccount(fromAccNum);
        if(fromAcc == null || !fromAcc.getOwnerUsername().equals(loggedInUser.getUsername())){
             System.out.println("Invalid source account number or you do not own this account.");
             return;
        }

        if (bankService.transfer(fromAccNum, toAccNum, amount)) {
            System.out.println("Transfer successful.");
        } else {
            System.out.println("Transfer failed. Check account numbers and balance.");
        }
    }

    private static void handleViewHistory() {
        System.out.print("Enter account number to view history: ");
        String accNum = scanner.nextLine();
        Account acc = bankService.getAccount(accNum);

        if(acc != null && acc.getOwnerUsername().equals(loggedInUser.getUsername())){
            System.out.println("\n--- Transaction History for " + accNum + " ---");
            acc.getTransactionHistory().forEach(System.out::println);
            System.out.println("----------------------------------------");
        } else {
            System.out.println("Invalid account number or you do not own this account.");
        }
    }
}