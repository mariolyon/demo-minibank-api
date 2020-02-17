package minibank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountDescription implements Comparable<AccountDescription> {
    final Id id;
    final Amount amount;

    static final AccountDescription NULL =  new AccountDescription(Id.NULL, Amount.NULL);

    @JsonCreator
    AccountDescription(@JsonProperty("id") Id id, @JsonProperty("amount") Amount amount) {
        this.id = id;
        this.amount = amount;
    }

    public Id getId() {
        return id;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public int compareTo(AccountDescription that) {
        return this.id.compareTo(that.id);
    }
}
