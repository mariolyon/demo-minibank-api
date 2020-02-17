package minibank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Amount {
    @JsonValue
    private final long value;

    @JsonCreator
    public Amount(long value) {
        this.value = value;
    }

    public Amount plus(Amount amount) {
        return new Amount(this.value + amount.value);
    }

    public static Amount of(long value) {
        return new Amount(value);
    }
}
