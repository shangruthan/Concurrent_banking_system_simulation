import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.List;

public class BankService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public boolean registerUser(String username, String password) {
        return users.putIfAbsent(username, new User(username, password)) == null;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    public Account createAccount(String username, double initialDeposit) {
        String accountNumber = UUID.randomUUID().toString();
        Account newAccount = new Account(accountNumber, username, initialDeposit);
        accounts.put(accountNumber, newAccount);
        return newAccount;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public List<Account> getAccountsForUser(String username) {
        return accounts.values().stream()
            .filter(acc -> acc.getOwnerUsername().equals(username))
            .collect(Collectors.toList());
    }

    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account fromAccount = accounts.get(fromAccountNumber);
        Account toAccount = accounts.get(toAccountNumber);

        if (fromAccount == null || toAccount == null || amount <= 0) {
            return false;
        }

        Account lock1 = System.identityHashCode(fromAccount) < System.identityHashCode(toAccount) ? fromAccount : toAccount;
        Account lock2 = System.identityHashCode(fromAccount) < System.identityHashCode(toAccount) ? toAccount : fromAccount;

        lock1.getLock().lock();
        try {
            lock2.getLock().lock();
            try {
                if (fromAccount.withdraw(amount)) {
                    toAccount.deposit(amount);
                    return true;
                }
                return false;
            } finally {
                lock2.getLock().unlock();
            }
        } finally {
            lock1.getLock().unlock();
        }
    }
}