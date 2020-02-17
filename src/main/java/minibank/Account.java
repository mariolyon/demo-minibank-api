package minibank;

public class Account implements Comparable<Account> {
    final Id id;
    Amount amount;

    Account(Id id, Amount amount) {
        this.id = id;
        this.amount = amount;
    }

    public AccountDescription describe() {
        return new AccountDescription(id, amount);
    }

    @Override
    public int compareTo(Account that) {
        return this.id.compareTo(that.id);
    }
}

