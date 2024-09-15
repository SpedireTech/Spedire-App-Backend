package com.spedire.Spedire.enums;

public enum OrderType {

    AWAITING_MATCH("Awaiting_Match"), FOUND_MATCH("Found_Match"), ACCEPTED("Accepted"),
    COMPLETED("completed"), UNKNOWN("Unknown");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

}
