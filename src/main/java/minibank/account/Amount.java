package minibank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Amount {
    @JsonValue
    public final long value;

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

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return value == amount.value;
    }
}
