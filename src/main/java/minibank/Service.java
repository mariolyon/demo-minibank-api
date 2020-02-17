package minibank;

import minibank.account.Account;
import minibank.account.Amount;
import minibank.account.Id;
import minibank.dto.AccountDescription;
import minibank.error.AppError;

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

    AccountDescription createAccount() {
        return describe(accounts.createAccount());
    }

    private static AccountDescription describe(Account account) {
        return new AccountDescription(account.id, account.amount);
    }

    Optional<AppError> deposit(Id id, Amount amount) {
        return accounts.get(id).map(a -> {
                    a.deposit(amount);
                    return Optional.<AppError>empty();
                }
        ).orElse(Optional.of(AppError.ACCOUNT_DOES_NOT_EXIST));
    }
}
