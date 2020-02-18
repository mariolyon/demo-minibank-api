package minibank.account;

public class Account {
    public final Id id;
    public Amount amount;

    public Account(Id id, Amount amount) {
        this.id = id;
        this.amount = amount;
    }

    public synchronized void deposit(Amount amount) {
        this.amount = this.amount.plus(amount);
    }
}

