package com.spedire.Spedire.enums;

public enum OrderType {

    AWAITING_MATCH("Awaiting_Match"), FOUND_MATCH("Fond_Match"), ACCEPTED("Accepted"),
    COMPLETED("FAILED"), UNKNOWN("UNKNOWN");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

}
