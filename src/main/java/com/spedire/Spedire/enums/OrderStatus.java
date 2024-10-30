package com.spedire.Spedire.enums;

public enum OrderStatus {

    AWAITING_MATCH("Awaiting_Match"), FOUND_MATCH("Found_Match"), ACCEPTED("Accepted"),
    COMPLETED("completed"), UNKNOWN("Unknown");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

}
