package com.viveknaskar;

/**
 * Enum for the Field indexes
 */
public enum RedisIndex {
    GUID(0),
    FIRSTNAME(1),
    LASTNAME(2),
    DOB(3),
    POSTAL_CODE(4);

    private final Integer value;

    RedisIndex(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
