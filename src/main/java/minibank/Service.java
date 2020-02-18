package minibank;

import minibank.account.Account;
import minibank.account.Amount;
import minibank.account.Id;
import minibank.dto.AccountDescription;
import minibank.error.AppError;
import minibank.error.ResultOrError;

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

    ResultOrError<AccountDescription> deposit(Id id, Amount amount) {
        return accounts.get(id).map(a -> {
                    a.deposit(amount);
                    return new ResultOrError(describe(a));
                }
        ).orElse(new ResultOrError(AppError.ACCOUNT_DOES_NOT_EXIST));
    }

    ResultOrError<List<AccountDescription>> transfer(Id id, Id recipient, Amount amount) {
        Optional<Account> maybeFrom = accounts.get(id);
        Optional<Account> maybeTo = accounts.get(recipient);

        if (maybeFrom.isPresent() && maybeTo.isPresent()) {
            Account from = maybeFrom.get();
            from.deposit(Amount.of(- amount.value));
            Account to = maybeTo.get();
            to.deposit(Amount.of(amount.value));
            return new ResultOrError(List.of(describe(from), describe(to)));
        } else {
            return new ResultOrError(AppError.ACCOUNT_DOES_NOT_EXIST);
        }
    }
}
