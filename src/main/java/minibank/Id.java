package minibank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Id implements Comparable<Id> {
    @JsonValue
    private final Long value;

    static final Id NULL = new Id(-1);


    static Id of(long value) {
        return new Id(value);
    }

    @JsonCreator
    Id(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(Id that) {
        return this.value.compareTo(that.value);
    }
}
