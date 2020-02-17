package minibank;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Accounts {
    private HashMap<Id, Account> accounts = new HashMap<>();

    private long nextAccountNumber = 1;

    synchronized Id createAccount() {
        Id id = new Id(nextAccountNumber);
        Account account = new Account(id, new Amount(0));
        accounts.put(id, account);
        nextAccountNumber++;
        return account.id;
    }

    Optional<Account> get(Id id) {
        return Optional.ofNullable(accounts.get(id));
    }

    Collection<Account> list() {
        return accounts.values();
    }
}
