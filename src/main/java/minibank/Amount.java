package minibank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Amount {
    @JsonValue
    private final long value;

    static final Amount NULL = new Amount(0);

    @JsonCreator
    Amount(long value) {
        this.value = value;
    }
}
