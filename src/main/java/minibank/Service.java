package minibank;

import minibank.account.Account;
import minibank.account.Id;
import minibank.dto.AccountDescription;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Service {
    private final Accounts accounts;

    Service(Accounts accounts) {
        this.accounts = accounts;
    }

    List<AccountDescription> describeAccounts() {
       return accounts.list().stream().map(Service::describe).sorted().collect(Collectors.toList());
    }

    Optional<AccountDescription> describeAccount(Id id) {
        return accounts.get(id).map(Service::describe);
    }

    private static AccountDescription describe(Account account) {
        return new AccountDescription(account.id, account.amount);
    }

}
