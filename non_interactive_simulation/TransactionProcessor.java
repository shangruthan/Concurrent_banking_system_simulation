import java.util.concurrent.BlockingQueue;

public class TransactionProcessor implements Runnable {
    private final BlockingQueue<Transaction> queue;

    public TransactionProcessor(BlockingQueue<Transaction> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Transaction transaction = queue.take();
                if (transaction.getType() == null) {
                    break;
                }
                process(transaction);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
                handleDeposit(transaction);
                break;
            case WITHDRAWAL:
                handleWithdrawal(transaction);
                break;
            case TRANSFER:
                handleTransfer(transaction);
                break;
        }
    }
    
    private void handleDeposit(Transaction t) {
        t.getToAccount().deposit(t.getAmount());
        t.setStatus(TransactionStatus.COMPLETED);
    }
    
    private void handleWithdrawal(Transaction t) {
        if (t.getFromAccount().withdraw(t.getAmount())) {
            t.setStatus(TransactionStatus.COMPLETED);
        } else {
            t.setStatus(TransactionStatus.FAILED);
        }
    }

    private void handleTransfer(Transaction t) {
        Account acc1 = t.getFromAccount();
        Account acc2 = t.getToAccount();

        Account lock1 = System.identityHashCode(acc1) < System.identityHashCode(acc2) ? acc1 : acc2;
        Account lock2 = System.identityHashCode(acc1) < System.identityHashCode(acc2) ? acc2 : acc1;

        lock1.getLock().lock();
        try {
            lock2.getLock().lock();
            try {
                if (acc1.withdraw(t.getAmount())) {
                    acc2.deposit(t.getAmount());
                    t.setStatus(TransactionStatus.COMPLETED);
                } else {
                    t.setStatus(TransactionStatus.FAILED);
                }
            } finally {
                lock2.getLock().unlock();
            }
        } finally {
            lock1.getLock().unlock();
        }
    }
}