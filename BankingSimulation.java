import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BankingSimulation {

    private static final int NUM_ACCOUNTS = 10;
    private static final int NUM_THREADS = 5;
    private static final double INITIAL_BALANCE = 1000.0;
    private static final int NUM_TRANSACTIONS = 10000;

    public static void main(String[] args) throws InterruptedException {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < NUM_ACCOUNTS; i++) {
            accounts.add(new Account("ACC-" + i, INITIAL_BALANCE));
        }

        double initialTotalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.printf("Initial total balance: $%,.2f%n", initialTotalBalance);
        System.out.println("-------------------------------------------");

        BlockingQueue<Transaction> queue = new LinkedBlockingQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(new TransactionProcessor(queue));
        }

        Random random = new Random();
        for (int i = 0; i < NUM_TRANSACTIONS; i++) {
            Account fromAccount = accounts.get(random.nextInt(NUM_ACCOUNTS));
            Account toAccount = accounts.get(random.nextInt(NUM_ACCOUNTS));
            double amount = random.nextDouble() * 100;

            if (fromAccount != toAccount) {
                queue.put(new Transaction(TransactionType.TRANSFER, fromAccount, toAccount, amount));
            }
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            queue.put(new Transaction(null, null, null, 0));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("-------------------------------------------");
        System.out.println("All transactions processed.");

        double finalTotalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.printf("Final total balance:   $%,.2f%n", finalTotalBalance);

        System.out.println("\n--- AUDIT ---");
        if (Math.abs(initialTotalBalance - finalTotalBalance) < 0.0001) {
            System.out.println("SUCCESS: System integrity maintained. Total balance is consistent.");
        } else {
            System.out.println("FAILURE: System integrity compromised. Balance mismatch detected.");
        }
    }
}