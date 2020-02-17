package minibank;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Service {
    private final Accounts accounts;

    Service(Accounts accounts) {
        this.accounts = accounts;
    }

    List<AccountDescription> describeAccounts() {
       return accounts.list().stream().map(Account::describe).sorted().collect(Collectors.toList());
    }

    Optional<AccountDescription> describeAccount(Id id) {
        return accounts.get(id).map(Account::describe);
    }

}
