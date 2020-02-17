package minibank.account;

import minibank.dto.AccountDescription;

public class Account {
    public final Id id;
    public Amount amount;

    public Account(Id id, Amount amount) {
        this.id = id;
        this.amount = amount;
    }
}

