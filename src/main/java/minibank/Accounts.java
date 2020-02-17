package minibank;

import minibank.account.Account;
import minibank.account.Amount;
import minibank.account.Id;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class Accounts {
    private HashMap<Id, Account> accounts = new HashMap<>();

    private long nextAccountNumber = 1;

    synchronized Account createAccount() {
        Id id = Id.of(nextAccountNumber);
        Account account = new Account(id, new Amount(0));
        accounts.put(id, account);
        nextAccountNumber++;
        return account;
    }

    Optional<Account> get(Id id) {
        return Optional.ofNullable(accounts.get(id));
    }

    Collection<Account> list() {
        return accounts.values();
    }
}
