public class Transaction {
    private final TransactionType type;
    private final Account fromAccount;
    private final Account toAccount;
    private final double amount;
    private TransactionStatus status;

    public Transaction(TransactionType type, Account fromAccount, Account toAccount, double amount) {
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
    }

    public TransactionType getType() { return type; }
    public Account getFromAccount() { return fromAccount; }
    public Account getToAccount() { return toAccount; }
    public double getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}