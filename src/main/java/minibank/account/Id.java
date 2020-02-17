package minibank.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Id implements Comparable<Id> {
    @JsonValue
    private final Long value;

    public static Id of(long value) {
        return new Id(value);
    }

    @JsonCreator
    private Id(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(Id that) {
        return this.value.compareTo(that.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id = (Id) o;
        return Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
